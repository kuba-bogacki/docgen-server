package com.document.service.implementation;

import com.document.config.docx.DocxReaderConfiguration;
import com.document.exception.CompanyNotFoundException;
import com.document.exception.EvidenceNotFoundException;
import com.document.exception.UserNotFoundException;
import com.document.mapper.EvidenceMapper;
import com.document.model.Evidence;
import com.document.model.dto.*;
import com.document.repository.EvidenceRepository;
import com.document.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.document.model.type.EvidenceType.FINANCIAL_STATEMENT;
import static com.document.util.ApplicationConstants.*;
import static com.document.util.UrlBuilder.addTokenHeader;
import static com.document.util.UrlBuilder.buildUrl;

@Log4j2
@Service
@RequiredArgsConstructor
public class EvidenceServiceImplementation implements EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final EvidenceMapper evidenceMapper;
    private final DocxReaderConfiguration docxReader;
    private final WebClient.Builder webClientBuilder;

    @Override
    public void deleteEvidenceById(String evidenceId) {
        var evidenceEntity = evidenceRepository.findByEvidenceId(evidenceId);

        if (Optional.ofNullable(evidenceEntity).isEmpty()) {
            log.warn("Couldn't delete entity because evidence with provided id [{}] is not exist", evidenceId);
            throw new EvidenceNotFoundException(evidenceId);
        }
        evidenceRepository.delete(evidenceEntity);
    }

    @Override
    public void createFinancialStatement(FinancialStatementDto financialStatementDto, String jwtToken) {
        var currentUserDto = getCurrentUserDto(jwtToken);

        if (Optional.ofNullable(currentUserDto).isEmpty()) {
            throw new UserNotFoundException("Couldn't find current user with provided id in database");
        }
        var currentCompanyDto = getCurrentCompanyDto(jwtToken, financialStatementDto.getCompanyId());

        if (Optional.ofNullable(currentCompanyDto).isEmpty()) {
            throw new CompanyNotFoundException("Couldn't find company with provided id in database");
        }
        final var placeholders = createPlaceholdersMap(financialStatementDto, currentCompanyDto, currentUserDto);
        var document = docxReader.generateDocument(placeholders, FINANCIAL_STATEMENT_FILE_NAME);

        final var evidence = Evidence.builder()
                .evidenceType(FINANCIAL_STATEMENT)
                .evidenceName(currentCompanyDto.getCompanyName())
                .companyId(financialStatementDto.getCompanyId())
                .evidenceContent(document.getContent())
                .build();
        final var savedEvidenceEntity = evidenceRepository.save(evidence);
        log.info("Evidence has been created with id : {}", savedEvidenceEntity.getEvidenceId());
    }

    @Override
    public EvidenceDetailsDto getEvidenceDetailsById(String evidenceId) {
        var evidenceEntity = evidenceRepository.findByEvidenceId(evidenceId);

        if (Optional.ofNullable(evidenceEntity).isEmpty()) {
            log.warn("Evidence with provided id [{}] not exist", evidenceId);
            throw new EvidenceNotFoundException(evidenceId);
        }
        return evidenceMapper.mapToDetailDto(evidenceEntity);
    }

    @Override
    public List<EvidenceDto> getAllCompanyEvidences(String companyId) {
        return evidenceRepository.findAllByCompanyId(companyId).stream()
                .sorted(Comparator.comparing(Evidence::getCreateDateTime).reversed())
                .map(evidenceMapper::mapToDto)
                .toList();
    }

    private Map<String, String> createPlaceholdersMap(FinancialStatementDto financialStatementDto, CompanyDto companyDto, UserDto userDto) {
        return new HashMap<>() {{
            put("periodStartDate", financialStatementDto.getPeriodStartDate());
            put("periodEndDate", financialStatementDto.getPeriodEndDate());
            put("companyEquity", String.valueOf(financialStatementDto.getCompanyEquity()));
            put("companyTotalSum", String.valueOf(financialStatementDto.getCompanyTotalSum()));
            put("companyNetProfit", String.valueOf(financialStatementDto.getCompanyNetProfit()));
            put("companyNetIncrease", String.valueOf(financialStatementDto.getCompanyNetIncrease()));
            put("managementBoardPresident", financialStatementDto.getManagementBoardPresident());
            put("supervisoryBoardChairman", financialStatementDto.getSupervisoryBoardChairman());
            put("supervisoryBoardMembers", getMembers(financialStatementDto.getSupervisoryBoardMembers()));
            put("addressStreetName", companyDto.getCompanyAddressDto().getAddressStreetName());
            put("addressStreetNumber", companyDto.getCompanyAddressDto().getAddressStreetNumber());
            put("addressLocalNumber", getAddressLocalNumber(companyDto));
            put("addressPostalCode", companyDto.getCompanyAddressDto().getAddressPostalCode());
            put("addressCity", companyDto.getCompanyAddressDto().getAddressCity());
            put("companyName", companyDto.getCompanyName());
            put("companyKrsNumber", companyDto.getCompanyKrsNumber());
            put("companyRegonNumber", String.valueOf(companyDto.getCompanyRegonNumber()));
            put("companyNipNumber", String.valueOf(companyDto.getCompanyNipNumber()));
            put("currentUser", concatCurrentUserNames(userDto));
            put("currentDate", parseCustomLocalDate());
        }};
    }

    private String getMembers(List<String> supervisoryBoardMembers) {
        return supervisoryBoardMembers.isEmpty() ? StringUtils.EMPTY : String.join(", \n", supervisoryBoardMembers);
    }

    private String getAddressLocalNumber(CompanyDto companyDto) {
        return companyDto.getCompanyAddressDto().getAddressLocalNumber() != null ?
                companyDto.getCompanyAddressDto().getAddressLocalNumber() : StringUtils.EMPTY;
    }

    private String concatCurrentUserNames(UserDto userDto) {
        return (userDto.getUserFirstNameI() + " " +
                (userDto.getUserFirstNameII() != null ? userDto.getUserFirstNameII() + " " : StringUtils.EMPTY) +
                userDto.getUserLastNameI() + " " +
                (userDto.getUserLastNameII() != null ? userDto.getUserLastNameII() : StringUtils.EMPTY)).trim();
    }

    private String parseCustomLocalDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN));
    }

    private UserDto getCurrentUserDto(String jwtToken) {
        return webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, "authentication-service", API_VERSION, "/authentication/user"))
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    private CompanyDto getCurrentCompanyDto(String jwtToken, String companyId) {
        return webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, "company-service", API_VERSION, "/company/details/" + companyId))
                .retrieve()
                .bodyToMono(CompanyDto.class)
                .block();
    }
}

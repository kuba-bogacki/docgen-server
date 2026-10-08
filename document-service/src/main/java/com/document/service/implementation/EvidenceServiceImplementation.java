package com.document.service.implementation;

import com.document.exception.CompanyNotFoundException;
import com.document.exception.EvidenceNotFoundException;
import com.document.exception.UserNotFoundException;
import com.document.infrastructure.HttpClient;
import com.document.mapper.EvidenceMapper;
import com.document.model.Evidence;
import com.document.model.dto.*;
import com.document.model.type.EvidenceStatus;
import com.document.model.type.EvidenceType;
import com.document.queue.MessageQueuePublisher;
import com.document.repository.EvidenceRepository;
import com.document.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.document.util.ApplicationConstants.DEFAULT_DATE_PATTERN;
import static com.document.util.ApplicationConstants.FINANCIAL_STATEMENT_FILE_NAME;

@Log4j2
@Service
@RequiredArgsConstructor
public class EvidenceServiceImplementation implements EvidenceService {

    private final HttpClient httpClient;
    private final EvidenceMapper evidenceMapper;
    private final EvidenceRepository evidenceRepository;
    private final MessageQueuePublisher messageQueuePublisher;

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
    public void createFinancialStatement(FinancialStatementDto financialStatementDto, String userEmail) {
        var currentUserDto = httpClient.getCurrentUserDto(userEmail);

        if (Optional.ofNullable(currentUserDto).isEmpty()) {
            throw new UserNotFoundException("Couldn't find current user with provided id in database");
        }
        var currentCompanyDto = httpClient.getCurrentCompanyDto(financialStatementDto.getCompanyId(), userEmail);

        if (Optional.ofNullable(currentCompanyDto).isEmpty()) {
            throw new CompanyNotFoundException("Couldn't find company with provided id in database");
        }

        final var evidence = Evidence.builder()
                .evidenceType(EvidenceType.FINANCIAL_STATEMENT)
                .evidenceStatus(EvidenceStatus.DRAFTED)
                .evidenceName(currentCompanyDto.getCompanyName())
                .companyId(financialStatementDto.getCompanyId())
                .build();
        final var savedEntity = evidenceRepository.save(evidence);
        log.info("Drafted evidence has been saved with id : {}", savedEntity.getEvidenceId());

        final var placeholders = createPlaceholdersMap(financialStatementDto, currentCompanyDto, currentUserDto);
        final var queueMessage = QueueMessage.builder()
                .evidenceId(savedEntity.getEvidenceId())
                .userEmail(userEmail)
                .evidenceName(savedEntity.getEvidenceName())
                .templateFileName(FINANCIAL_STATEMENT_FILE_NAME)
                .placeholders(placeholders)
                .build();
        messageQueuePublisher.publishEvidenceDrafted(queueMessage);
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
        final Map<String, String> placeholdersMap = new HashMap<>();
        placeholdersMap.put("periodStartDate", financialStatementDto.getPeriodStartDate());
        placeholdersMap.put("periodEndDate", financialStatementDto.getPeriodEndDate());
        placeholdersMap.put("companyEquity", String.valueOf(financialStatementDto.getCompanyEquity()));
        placeholdersMap.put("companyTotalSum", String.valueOf(financialStatementDto.getCompanyTotalSum()));
        placeholdersMap.put("companyNetProfit", String.valueOf(financialStatementDto.getCompanyNetProfit()));
        placeholdersMap.put("companyNetIncrease", String.valueOf(financialStatementDto.getCompanyNetIncrease()));
        placeholdersMap.put("managementBoardPresident", financialStatementDto.getManagementBoardPresident());
        placeholdersMap.put("supervisoryBoardChairman", financialStatementDto.getSupervisoryBoardChairman());
        placeholdersMap.put("supervisoryBoardMembers", getMembers(financialStatementDto.getSupervisoryBoardMembers()));
        placeholdersMap.put("addressStreetName", companyDto.getCompanyAddressDto().getAddressStreetName());
        placeholdersMap.put("addressStreetNumber", companyDto.getCompanyAddressDto().getAddressStreetNumber());
        placeholdersMap.put("addressLocalNumber", getAddressLocalNumber(companyDto));
        placeholdersMap.put("addressPostalCode", companyDto.getCompanyAddressDto().getAddressPostalCode());
        placeholdersMap.put("addressCity", companyDto.getCompanyAddressDto().getAddressCity());
        placeholdersMap.put("companyName", companyDto.getCompanyName());
        placeholdersMap.put("companyKrsNumber", companyDto.getCompanyKrsNumber());
        placeholdersMap.put("companyRegonNumber", String.valueOf(companyDto.getCompanyRegonNumber()));
        placeholdersMap.put("companyNipNumber", String.valueOf(companyDto.getCompanyNipNumber()));
        placeholdersMap.put("currentUser", concatCurrentUserNames(userDto));
        placeholdersMap.put("currentDate", parseCustomLocalDate());
        return placeholdersMap;
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
        return LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN));
    }
}

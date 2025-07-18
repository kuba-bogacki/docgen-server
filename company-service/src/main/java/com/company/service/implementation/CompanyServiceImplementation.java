package com.company.service.implementation;

import com.company.exception.CompanyMemberAdditionException;
import com.company.exception.CompanyNonExistException;
import com.company.mapper.CompanyMapper;
import com.company.model.Company;
import com.company.model.dto.CompanyDto;
import com.company.model.dto.UserDto;
import com.company.repository.CompanyRepository;
import com.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.company.util.ApplicationConstants.API_VERSION;
import static com.company.util.ApplicationConstants.PROTOCOL;
import static com.company.util.UrlBuilder.addTokenHeader;
import static com.company.util.UrlBuilder.buildUrl;

@Service
@RequiredArgsConstructor
@Log4j2
public class CompanyServiceImplementation implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final WebClient.Builder webClientBuilder;

    @Override
    public Boolean checkIfCompanyAlreadyExist(String companyKrsNumber) {
        Optional<Company> foundedCompany = companyRepository.findCompanyByCompanyKrsNumber(companyKrsNumber);
        return foundedCompany.isPresent();
    }

    @Override
    public CompanyDto createCompany(CompanyDto companyDto, String jwtToken) {
        UUID currentUserId = getCurrentUserId(jwtToken);
        companyDto.getCompanyMembers().add(currentUserId);
        Company company = companyRepository.save(companyMapper.mapToEntity(companyDto));
        log.debug("Company has been created with id : {}", company.getCompanyId());
        return companyMapper.mapToDto(company);
    }

    @Override
    public CompanyDto getCompanyByName(String companyName) throws CompanyNonExistException {
        Optional<Company> companyEntity = companyRepository.findCompanyByCompanyName(companyName);

        if (companyEntity.isEmpty()) {
            throw new CompanyNonExistException("Couldn't find company using provide company name");
        }
        return companyMapper.mapToDto(companyEntity.get());
    }

    @Override
    public CompanyDto getCompanyByCompanyId(String companyId) {
        Optional<Company> companyEntity = companyRepository.findCompanyByCompanyId(UUID.fromString(companyId));

        if (companyEntity.isEmpty()) {
            throw new CompanyNonExistException("Couldn't find company using provide id");
        }
        return companyMapper.mapToDto(companyEntity.get());
    }

    @Override
    public CompanyDto getCompanyByCompanyKrsNumber(String companyKrsNumber) {
        Optional<Company> companyEntity = companyRepository.findCompanyByCompanyKrsNumber(companyKrsNumber);

        if (companyEntity.isEmpty()) {
            throw new CompanyNonExistException("Couldn't find company using provide krs number");
        }
        return companyMapper.mapToDto(companyEntity.get());
    }

    @Override
    public CompanyDto updateCompany(CompanyDto companyDto) {
        //TODO
        return null;
    }

    @Override
    public List<CompanyDto> getCurrentUserCompanies(String jwtToken) {
        UUID currentUserId = getCurrentUserId(jwtToken);
        List<Company> companyList = companyRepository.findCompaniesByCompanyMembersContaining(currentUserId);
        return companyMapper.mapToDtos(companyList);
    }

    @Override
    public Set<UUID> getCompanyMemberIdList(String companyId) {
        Optional<Company> entity = companyRepository.findCompanyByCompanyId(UUID.fromString(companyId));

        if (entity.isEmpty()) {
            throw new CompanyNonExistException("Company with provided id is not exist");
        }
        return entity.get().getCompanyMembers();
    }

    @Override
    public void addNewMemberToCompany(String companyId, String userId) {
        Optional<Company> entity = companyRepository.findCompanyByCompanyId(UUID.fromString(companyId));

        if (entity.isEmpty()) {
            throw new CompanyMemberAdditionException("New member addition failed, company with provided id is not exist");
        }
        entity.get().getCompanyMembers().add(UUID.fromString(userId));
        companyRepository.save(entity.get());
    }

    @Override
    public List<UserDto> getDetailMembersList(String companyId, String jwtToken) throws CompanyNonExistException {
        Optional<Company> entity = companyRepository.findCompanyByCompanyId(UUID.fromString(companyId));

        if (entity.isEmpty()) {
            throw new CompanyNonExistException("Company with provided id is not exist");
        }
        return entity.get().getCompanyMembers().stream()
                .map(memberId -> getAuthenticationServiceUserDto(memberId.toString(), jwtToken))
                .toList();
    }

    private UUID getCurrentUserId(String jwtToken) {
        return webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, "authentication-service", API_VERSION, "/authentication/get-id"))
                .retrieve()
                .bodyToMono(UUID.class)
                .block();
    }

    private UserDto getAuthenticationServiceUserDto(String userId, String jwtToken) {
        return webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, "authentication-service", API_VERSION, "/authentication/get-by-id/" + userId))
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }
}

package com.company.service.implementation;

import com.company.exception.CompanyNonExistException;
import com.company.mapper.CompanyMapper;
import com.company.model.Company;
import com.company.model.dto.CompanyDto;
import com.company.model.dto.UserDto;
import com.company.repository.CompanyRepository;
import com.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

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
        Optional<Company> foundedCompany = companyRepository.findAll().stream()
                .filter(company -> company.getCompanyKrsNumber().equals(companyKrsNumber))
                .findAny();
        return foundedCompany.isPresent();
    }

    @Override
    public CompanyDto createCompany(CompanyDto companyDto, String jwtToken) {
        UUID currentUserId = getCurrentUserId(jwtToken);
        companyDto.getCompanyMembers().add(currentUserId);
        Company company = companyRepository.save(companyMapper.mapToEntity(companyDto));
        log.info("Company has been created with id : {}", company.getCompanyId());
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
    public CompanyDto getCompanyByCompanyKrsNumber(String krsNumber) {
        Optional<Company> foundedCompany = companyRepository.findAll().stream()
                .filter(company -> company.getCompanyKrsNumber().equals(krsNumber))
                .findAny();

        if (foundedCompany.isEmpty()) {
            throw new CompanyNonExistException("Couldn't find company using provide krs number");
        }
        return companyMapper.mapToDto(foundedCompany.get());
    }

    @Override
    public CompanyDto updateCompany(CompanyDto companyDto) {
        Optional<Company> companyEntity = companyRepository.findCompanyByCompanyId(companyDto.getCompanyId());
//        companyRepository.
        return null;
    }

    @Override
    public List<CompanyDto> getCurrentUserCompanies(String jwtToken) {
        UUID currentUserId = getCurrentUserId(jwtToken);
        List<Company> companyList = companyRepository.findAll().stream()
                .filter(company -> company.getCompanyMembers().contains(currentUserId))
                .toList();
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
            throw new CompanyNonExistException("Company with provided id is not exist");
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

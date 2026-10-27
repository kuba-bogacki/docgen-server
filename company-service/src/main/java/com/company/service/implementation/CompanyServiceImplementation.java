package com.company.service.implementation;

import com.company.exception.CompanyMemberAdditionException;
import com.company.exception.CompanyNonExistException;
import com.company.infrastructure.HttpClient;
import com.company.mapper.CompanyMapper;
import com.company.model.dto.CompanyDto;
import com.company.model.dto.UserDto;
import com.company.repository.CompanyRepository;
import com.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImplementation implements CompanyService {

    private final HttpClient httpClient;
    private final CompanyMapper companyMapper;
    private final CompanyRepository companyRepository;

    @Override
    public Boolean checkIfCompanyAlreadyExist(String companyKrsNumber) {
        return companyRepository.findCompanyByCompanyKrsNumber(companyKrsNumber).isPresent();
    }

    @Override
    @Transactional
    public CompanyDto createCompany(CompanyDto companyDto, String userEmail) {
        final var currentUserId = httpClient.getCurrentUserId(userEmail);
        companyDto.getCompanyMembers().add(currentUserId);

        final var company = companyRepository.save(companyMapper.mapToEntity(companyDto));
        log.info("Company has been created with id : {}", company.getCompanyId());
        return companyMapper.mapToDto(company);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDto getCompanyByName(String companyName) {
        final var companyEntity = companyRepository.findCompanyByCompanyName(companyName)
                .orElseThrow(() -> new CompanyNonExistException("Couldn't find company using provide company name"));

        return companyMapper.mapToDto(companyEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDto getCompanyByCompanyId(String companyId) {
        final var companyEntity = companyRepository.findCompanyByCompanyId(UUID.fromString(companyId))
                .orElseThrow(() -> new CompanyNonExistException("Couldn't find company using provide id"));

        return companyMapper.mapToDto(companyEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDto getCompanyByCompanyKrsNumber(String companyKrsNumber) {
        final var companyEntity = companyRepository.findCompanyByCompanyKrsNumber(companyKrsNumber)
                .orElseThrow(() -> new CompanyNonExistException("Couldn't find company using provide krs number"));

        return companyMapper.mapToDto(companyEntity);
    }

    @Override
    public CompanyDto updateCompany(CompanyDto companyDto) {
        //TODO
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDto> getCurrentUserCompanies(String userEmail) {
        final var currentUserId = httpClient.getCurrentUserId(userEmail);
        final var companyList = companyRepository.findCompaniesByCompanyMembersContaining(currentUserId);
        return companyMapper.mapToDtos(companyList);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> getCompanyMemberIdList(String companyId) {
        final var entity = companyRepository.findCompanyByCompanyId(UUID.fromString(companyId))
                .orElseThrow(() -> new CompanyNonExistException("Company with provided id is not exist"));

        return entity.getCompanyMembers();
    }

    @Override
    @Transactional
    public void addNewMemberToCompany(String companyId, String userId) {
        final var entity = companyRepository.findCompanyByCompanyId(UUID.fromString(companyId))
                .orElseThrow(() -> new CompanyMemberAdditionException("New member addition failed, company with provided id is not exist"));

        entity.getCompanyMembers().add(UUID.fromString(userId));
        final var savedCompany = companyRepository.save(entity);
        log.info("New member added and company with id {} has been successfully updated.", savedCompany.getCompanyId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getDetailMembersList(String companyId, String userEmail) {
        final var entity = companyRepository.findCompanyByCompanyId(UUID.fromString(companyId))
                .orElseThrow(() -> new CompanyNonExistException("Company with provided id is not exist"));

        return entity.getCompanyMembers().stream()
                .map(memberId -> httpClient.getAuthenticationServiceUserDto(memberId.toString(), userEmail))
                .toList();
    }
}

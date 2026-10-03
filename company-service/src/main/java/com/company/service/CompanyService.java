package com.company.service;

import com.company.model.dto.CompanyDto;
import com.company.model.dto.UserDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CompanyService {

    CompanyDto createCompany(CompanyDto companyDto, String userEmail);
    CompanyDto getCompanyByName(String companyName);
    CompanyDto getCompanyByCompanyId(String companyId);
    CompanyDto getCompanyByCompanyKrsNumber(String krsNumber);
    CompanyDto updateCompany(CompanyDto companyDto);
    List<CompanyDto> getCurrentUserCompanies(String userEmail);
    Boolean checkIfCompanyAlreadyExist(String companyKrsNumber);
    Set<UUID> getCompanyMemberIdList(String companyId);
    void addNewMemberToCompany(String companyId, String userId);
    List<UserDto> getDetailMembersList(String companyId, String userEmail);
}

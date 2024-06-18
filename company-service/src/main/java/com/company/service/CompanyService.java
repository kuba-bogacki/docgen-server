package com.company.service;

import com.company.exception.CompanyNonExistException;
import com.company.model.dto.CompanyDto;
import com.company.model.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface CompanyService {
    CompanyDto createCompany(CompanyDto companyDto, String jwtToken);
    CompanyDto getCompanyByName(String companyName) throws CompanyNonExistException;
    CompanyDto getCompanyByCompanyId(String companyId) throws CompanyNonExistException;
    CompanyDto getCompanyByCompanyKrsNumber(String krsNumber) throws CompanyNonExistException;
    CompanyDto updateCompany(CompanyDto companyDto);
    List<CompanyDto> getCurrentUserCompanies(String jwtToken);
    Boolean checkIfCompanyAlreadyExist(String companyKrsNumber);
    List<UUID> getCompanyMemberIdList(String companyId) throws CompanyNonExistException;
    void addNewMemberToCompany(String companyId, String userId);
    List<UserDto> getDetailMembersList(String companyId, String jwtToken) throws CompanyNonExistException;
}

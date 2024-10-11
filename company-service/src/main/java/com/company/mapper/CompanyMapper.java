package com.company.mapper;

import com.company.model.Company;
import com.company.model.dto.CompanyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface CompanyMapper {

    @Mapping(source = "companyAddressDto", target = "companyAddress")
    @Mapping(source = "companyRegistrationDate", target = "companyRegistrationDate", dateFormat = "dd.MM.yyyy")
    Company mapToEntity(CompanyDto companyDto);
    @Mapping(source = "companyAddress", target = "companyAddressDto")
    CompanyDto mapToDto(Company company);
    @Mapping(source = "companyAddressDto", target = "companyAddress")
    @Mapping(source = "companyRegistrationDate", target = "companyRegistrationDate", dateFormat = "dd.MM.yyyy")
    List<Company> mapToEntities(List<CompanyDto> companyDtos);
    @Mapping(source = "companyAddress", target = "companyAddressDto")
    List<CompanyDto> mapToDtos(List<Company> companies);
}

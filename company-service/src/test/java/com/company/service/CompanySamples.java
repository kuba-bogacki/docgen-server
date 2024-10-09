package com.company.service;

import com.company.model.Address;
import com.company.model.Company;
import com.company.model.dto.AddressDto;
import com.company.model.dto.CompanyDto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class CompanySamples {

    final static UUID currentUserId = UUID.fromString("5a00f387-2035-4e91-860e-33a1db0a64db");
    final static UUID companyId = UUID.fromString("c2f83a82-71df-4298-9989-363227d6ab79");
    final static String companyName = "Allegro sp. z o.o.";
    final static String companyKrsNumber = "0000635012";
    final static Long companyRegonNumber = 3_653_315_532L;
    final static Long companyNipNumber = 5_252_674_798L;
    final static LocalDate companyRegistrationDate = LocalDate.of(1999, 10, 3);
    final static String companyRegistrationDateStringFormat = "03.10.1999";
    final static Address companyAddress = new Address();
    final static AddressDto companyAddressDto = new AddressDto();
    final static Float companyShareCapital = 123_456_789.12F;
    final static Set<UUID> companyMembers = new HashSet<>();

    Company sampleEntityCompany = Company.builder()
            .companyName(companyName)
            .companyKrsNumber(companyKrsNumber)
            .companyRegonNumber(companyRegonNumber)
            .companyNipNumber(companyNipNumber)
            .companyRegistrationDate(companyRegistrationDate)
            .companyAddress(companyAddress)
            .companyShareCapital(companyShareCapital)
            .companyMembers(companyMembers)
            .build();

    CompanyDto sampleDtoCompany = CompanyDto.builder()
            .companyName(companyName)
            .companyKrsNumber(companyKrsNumber)
            .companyRegonNumber(companyRegonNumber)
            .companyNipNumber(companyNipNumber)
            .companyRegistrationDate(companyRegistrationDateStringFormat)
            .companyAddressDto(companyAddressDto)
            .companyShareCapital(companyShareCapital)
            .companyMembers(companyMembers)
            .build();
}

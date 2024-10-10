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
    final static UUID companyIdI = UUID.fromString("c2f83a82-71df-4298-9989-363227d6ab79");
    final static UUID companyIdII = UUID.fromString("52d40201-0e69-43fa-86b1-14b66ff9572a");
    final static UUID companyIdIII = UUID.fromString("767adfdc-0c97-49ab-a6c2-f3a4d77c5fe7");
    final static String companyNameI = "Allegro sp. z o.o.";
    final static String companyNameII = "Dell sp. z o.o.";
    final static String companyNameIII = "Amazon sp. z o.o.";
    final static String companyKrsNumberI = "0000635012";
    final static String companyKrsNumberII = "0025636077";
    final static String companyKrsNumberIII = "1100765016";
    final static Long companyRegonNumberI = 653_315_532L;
    final static Long companyRegonNumberII = 523_365_352L;
    final static Long companyRegonNumberIII = 534_322_532L;
    final static Long companyNipNumberI = 5_252_674_798L;
    final static Long companyNipNumberII = 5_276_611_798L;
    final static Long companyNipNumberIII = 7_242_664_798L;
    final static LocalDate companyRegistrationDateI = LocalDate.of(1999, 10, 3);
    final static LocalDate companyRegistrationDateII = LocalDate.of(2010, 5, 12);
    final static LocalDate companyRegistrationDateIII = LocalDate.of(2005, 1, 16);
    final static String companyRegistrationDateStringFormatI = "03.10.1999";
    final static String companyRegistrationDateStringFormatII = "12.5.2010";
    final static String companyRegistrationDateStringFormatIII = "16.1.2005";
    final static Address companyAddressI = new Address();
    final static Address companyAddressII = new Address();
    final static Address companyAddressIII = new Address();
    final static AddressDto companyAddressDtoI = new AddressDto();
    final static AddressDto companyAddressDtoII = new AddressDto();
    final static AddressDto companyAddressDtoIII = new AddressDto();
    final static Float companyShareCapitalI = 123_456_789.12F;
    final static Float companyShareCapitalII = 098_765_432.12F;
    final static Float companyShareCapitalIII = 789_345_123.12F;
    final static Set<UUID> companyMembersI = new HashSet<>();
    final static Set<UUID> companyMembersII = Set.of(currentUserId);
    final static Set<UUID> companyMembersIII = Set.of(currentUserId);

    Company sampleEntityCompanyI = Company.builder()
            .companyName(companyNameI)
            .companyKrsNumber(companyKrsNumberI)
            .companyRegonNumber(companyRegonNumberI)
            .companyNipNumber(companyNipNumberI)
            .companyRegistrationDate(companyRegistrationDateI)
            .companyAddress(companyAddressI)
            .companyShareCapital(companyShareCapitalI)
            .companyMembers(companyMembersI)
            .build();

    CompanyDto sampleDtoCompanyI = CompanyDto.builder()
            .companyName(companyNameI)
            .companyKrsNumber(companyKrsNumberI)
            .companyRegonNumber(companyRegonNumberI)
            .companyNipNumber(companyNipNumberI)
            .companyRegistrationDate(companyRegistrationDateStringFormatI)
            .companyAddressDto(companyAddressDtoI)
            .companyShareCapital(companyShareCapitalI)
            .companyMembers(companyMembersI)
            .build();

    Company sampleEntityCompanyII = Company.builder()
            .companyId(companyIdII)
            .companyName(companyNameII)
            .companyKrsNumber(companyKrsNumberII)
            .companyRegonNumber(companyRegonNumberII)
            .companyNipNumber(companyNipNumberII)
            .companyRegistrationDate(companyRegistrationDateII)
            .companyAddress(companyAddressII)
            .companyShareCapital(companyShareCapitalII)
            .companyMembers(companyMembersII)
            .build();

    CompanyDto sampleDtoCompanyII = CompanyDto.builder()
            .companyId(companyIdII)
            .companyName(companyNameII)
            .companyKrsNumber(companyKrsNumberII)
            .companyRegonNumber(companyRegonNumberII)
            .companyNipNumber(companyNipNumberII)
            .companyRegistrationDate(companyRegistrationDateStringFormatII)
            .companyAddressDto(companyAddressDtoII)
            .companyShareCapital(companyShareCapitalII)
            .companyMembers(companyMembersII)
            .build();

    Company sampleEntityCompanyIII = Company.builder()
            .companyId(companyIdIII)
            .companyName(companyNameIII)
            .companyKrsNumber(companyKrsNumberIII)
            .companyRegonNumber(companyRegonNumberIII)
            .companyNipNumber(companyNipNumberIII)
            .companyRegistrationDate(companyRegistrationDateIII)
            .companyAddress(companyAddressIII)
            .companyShareCapital(companyShareCapitalIII)
            .companyMembers(companyMembersIII)
            .build();

    CompanyDto sampleDtoCompanyIII = CompanyDto.builder()
            .companyId(companyIdIII)
            .companyName(companyNameIII)
            .companyKrsNumber(companyKrsNumberIII)
            .companyRegonNumber(companyRegonNumberIII)
            .companyNipNumber(companyNipNumberIII)
            .companyRegistrationDate(companyRegistrationDateStringFormatIII)
            .companyAddressDto(companyAddressDtoIII)
            .companyShareCapital(companyShareCapitalIII)
            .companyMembers(companyMembersIII)
            .build();
}

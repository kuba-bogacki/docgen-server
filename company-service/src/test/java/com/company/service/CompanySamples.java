package com.company.service;

import com.company.model.Address;
import com.company.model.Company;
import com.company.model.dto.AddressDto;
import com.company.model.dto.CompanyDto;
import com.company.model.dto.UserDto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

class CompanySamples {

    final UUID currentUserId = UUID.fromString("5a00f387-2035-4e91-860e-33a1db0a64db");
    final UUID companyMemberId = UUID.fromString("9b54793c-d655-4971-89f7-859cfa24beef");
    final UUID companyIdI = UUID.fromString("c2f83a82-71df-4298-9989-363227d6ab79");
    final UUID companyIdII = UUID.fromString("52d40201-0e69-43fa-86b1-14b66ff9572a");
    final UUID companyIdIII = UUID.fromString("767adfdc-0c97-49ab-a6c2-f3a4d77c5fe7");
    final String companyNameI = "Allegro sp. z o.o.";
    final String companyNameII = "Dell sp. z o.o.";
    final String companyNameIII = "Amazon sp. z o.o.";
    final String companyKrsNumberI = "0000635012";
    final String companyKrsNumberII = "0025636077";
    final String companyKrsNumberIII = "1100765016";
    final Long companyRegonNumberI = 653_315_532L;
    final Long companyRegonNumberII = 523_365_352L;
    final Long companyRegonNumberIII = 534_322_532L;
    final Long companyNipNumberI = 5_252_674_798L;
    final Long companyNipNumberII = 5_276_611_798L;
    final Long companyNipNumberIII = 7_242_664_798L;
    final LocalDate companyRegistrationDateI = LocalDate.of(1999, 10, 3);
    final LocalDate companyRegistrationDateII = LocalDate.of(2010, 5, 12);
    final LocalDate companyRegistrationDateIII = LocalDate.of(2005, 1, 16);
    final String companyRegistrationDateStringFormatI = "03.10.1999";
    final String companyRegistrationDateStringFormatII = "12.5.2010";
    final String companyRegistrationDateStringFormatIII = "16.1.2005";
    final Address companyAddressI = new Address();
    final Address companyAddressII = new Address();
    final Address companyAddressIII = new Address();
    final AddressDto companyAddressDtoI = new AddressDto();
    final AddressDto companyAddressDtoII = new AddressDto();
    final AddressDto companyAddressDtoIII = new AddressDto();
    final Float companyShareCapitalI = 123_456_789.12F;
    final Float companyShareCapitalII = 098_765_432.12F;
    final Float companyShareCapitalIII = 789_345_123.12F;
    final Set<UUID> companyMembersI = new HashSet<>();
    final Set<UUID> companyMembersII = Set.of(currentUserId);
    final Set<UUID> companyMembersIII = Set.of(currentUserId);

    final String userOneFirstNameI = "Jack";
    final String userOneFirstNameII = "John";
    final String userOneLastNameI = "Smith";
    final String userOneLastNameII = "Dowel";
    final String userOneEmail = "j.dowel@gmail.com";
    final String userTwoFirstNameI = "Lilly";
    final String userTwoFirstNameII = "Anna";
    final String userTwoLastNameI = "Gigi";
    final String userTwoLastNameII = "Blow";
    final String userTwoEmail = "g.blow@gmail.com";

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

    UserDto sampleUserDtoI = UserDto.builder()
            .userFirstNameI(userOneFirstNameI)
            .userFirstNameII(userOneFirstNameII)
            .userLastNameI(userOneLastNameI)
            .userLastNameII(userOneLastNameII)
            .userEmail(userOneEmail)
            .build();

    UserDto sampleUserDtoII = UserDto.builder()
            .userFirstNameI(userTwoFirstNameI)
            .userFirstNameII(userTwoFirstNameII)
            .userLastNameI(userTwoLastNameI)
            .userLastNameII(userTwoLastNameII)
            .userEmail(userTwoEmail)
            .build();

    List<UserDto> userDtoList = List.of(sampleUserDtoI, sampleUserDtoII);
}

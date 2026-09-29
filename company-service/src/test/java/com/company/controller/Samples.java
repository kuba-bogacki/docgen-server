package com.company.controller;

import com.company.model.dto.AddressDto;
import com.company.model.dto.CompanyDto;
import com.company.model.dto.UserDto;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class Samples {

    final String userId = "0af5b752-6f56-4542-8ac0-74557d7086b2";
    final String memberId = "1d2cbfe5-7fd5-487d-9a39-7b0a4eb98b91";
    final String addressId = "cdf356e0-8cc2-41db-a6bd-0697ec002418";
    final String companyId = "772148cf-139e-449f-a06a-3509faf84e79";
    final String userFirstNameI = "Jack";
    final String userFirstNameII = "John";
    final String userLastNameI = "Smith";
    final String userLastNameII = "Dowel";
    final String userEmail = "john.dowel@gmail.com";
    final String addressStreetName = "Nixon";
    final String addressStreetNumber = "14";
    final String addressLocalNumber = "55";
    final String addressPostalCode = "30-567";
    final String addressCity = "Milan";
    final String companyKrsNumber = "0000635012";
    final String companyName = "Allegro sp. z o.o.";
    final String userPhotoFileName = "photo_01.jpg";
    final Long companyRegonNumber = 653_315_532L;
    final Long companyNipNumber = 5_252_674_798L;
    final Float companyShareCapital = 123_456_789.12F;
    final String companyRegistrationDateStringFormat = "03.10.1999";
    final AddressDto companyAddressDto = new AddressDto();
    final Set<UUID> companyMembers = new HashSet<>();

    final AddressDto addressDto = AddressDto.builder()
            .addressId(UUID.fromString(addressId))
            .addressStreetName(addressStreetName)
            .addressStreetNumber(addressStreetNumber)
            .addressLocalNumber(addressLocalNumber)
            .addressPostalCode(addressPostalCode)
            .addressCity(addressCity)
            .build();

    final CompanyDto companyDto = CompanyDto.builder()
            .companyId(UUID.fromString(companyId))
            .companyName(companyName)
            .companyKrsNumber(companyKrsNumber)
            .companyRegonNumber(companyRegonNumber)
            .companyNipNumber(companyNipNumber)
            .companyRegistrationDate(companyRegistrationDateStringFormat)
            .companyAddressDto(companyAddressDto)
            .companyShareCapital(companyShareCapital)
            .companyMembers(companyMembers)
            .build();

    final UserDto userDto = UserDto.builder()
            .userFirstNameI(userFirstNameI)
            .userFirstNameII(userFirstNameII)
            .userLastNameI(userLastNameI)
            .userLastNameII(userLastNameII)
            .userPhotoFileName(userPhotoFileName)
            .userEmail(userEmail)
            .build();
}

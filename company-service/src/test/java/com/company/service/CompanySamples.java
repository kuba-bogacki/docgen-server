package com.company.service;

import com.company.model.Address;
import com.company.model.Company;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

class CompanySamples {

    final static UUID companyId = UUID.fromString("c2f83a82-71df-4298-9989-363227d6ab79");
    final static String companyName = "Allegro sp. z o.o.";
    final static String companyKrsNumber = "0000635012";
    final static Long companyRegonNumber = 365331553L;
    final static Long companyNipNumber = 5252674798L;
    final static LocalDate companyRegistrationDate = LocalDate.of(1999, 10, 3);
    final static Address companyAddress = new Address();
    final static Float companyShareCapital = 123456789.12F;
    final static Set<UUID> companyMembers = Set.of();

    Company sampleEntityCompany = Company.builder()
            .companyId(companyId)
            .companyName(companyName)
            .companyKrsNumber(companyKrsNumber)
            .companyRegonNumber(companyRegonNumber)
            .companyNipNumber(companyNipNumber)
            .companyRegistrationDate(companyRegistrationDate)
            .companyAddress(companyAddress)
            .companyShareCapital(companyShareCapital)
            .companyMembers(companyMembers)
            .build();
}

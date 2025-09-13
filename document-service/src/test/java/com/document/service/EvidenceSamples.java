package com.document.service;

import com.document.model.Evidence;
import com.document.model.dto.*;

import java.util.List;

import static com.document.model.type.EvidenceType.FINANCIAL_STATEMENT;

public class EvidenceSamples {

    final String evidenceIdNo1 = "28df7ad6-c0d4-4310-b4f1-4ede2d728495";
    final String companyIdNo1 = "90a8a7ff-bac0-4f6f-9f27-0464b42f21ee";
    final String evidenceName = "Financial_statement.pdf";
    final String managementBoardPresident = "Alfred Einstein";
    final String userFirstNameI = "Klass";
    final String userFirstNameII = "Anders";
    final String userLastNameI = "Star";
    final String supervisoryBoardChairman = "Martin Luther King";
    final String supervisoryBoardMembersNo1 = "Winston Churchill";
    final String supervisoryBoardMembersNo2 = "Andrea Bocelli";
    final String periodStartDate = "01-02-1990";
    final String periodEndDate = "31-12-1991";
    final String addressStreetName = "Lincoln St.";
    final String addressStreetNumber = "13";
    final String addressPostalCode = "30-040";
    final String addressCity = "Miami";
    final String companyName = "Allegro sp. z o.o.";
    final String companyKrsNumber = "0000635012";
    final long companyRegonNumber = 365331553L;
    final long companyNipNumber = 5252278252L;
    final float companyEquity = 1234.00F;
    final float companyTotalSum = 3456.12F;
    final float companyNetProfit = 3467.23F;
    final float companyNetIncrease = 3478.34F;
    final byte[] evidenceContent = {10, 20, 30, 40, 50, -128, 0, 127};

    Evidence evidenceEntity = Evidence.builder()
            .evidenceType(FINANCIAL_STATEMENT)
            .evidenceName(evidenceName)
            .companyId(companyIdNo1)
            .evidenceContent(evidenceContent)
            .build();

    FinancialStatementDto financialStatementDto = FinancialStatementDto.builder()
            .companyId(companyIdNo1)
            .periodStartDate(periodStartDate)
            .periodEndDate(periodEndDate)
            .companyEquity(companyEquity)
            .companyTotalSum(companyTotalSum)
            .companyNetProfit(companyNetProfit)
            .companyNetIncrease(companyNetIncrease)
            .managementBoardPresident(managementBoardPresident)
            .supervisoryBoardChairman(supervisoryBoardChairman)
            .supervisoryBoardMembers(List.of(supervisoryBoardMembersNo1, supervisoryBoardMembersNo2))
            .build();

    UserDto userDto = UserDto.builder()
            .userFirstNameI(userFirstNameI)
            .userFirstNameII(userFirstNameII)
            .userLastNameI(userLastNameI)
            .build();

    AddressDto addressDto = AddressDto.builder()
            .addressStreetName(addressStreetName)
            .addressStreetNumber(addressStreetNumber)
            .addressPostalCode(addressPostalCode)
            .addressCity(addressCity)
            .build();

    CompanyDto companyDto = CompanyDto.builder()
            .companyName(companyName)
            .companyKrsNumber(companyKrsNumber)
            .companyRegonNumber(companyRegonNumber)
            .companyNipNumber(companyNipNumber)
            .companyAddressDto(addressDto)
            .build();

    DocumentDto documentDto = DocumentDto.builder()
            .content(evidenceContent)
            .build();
}

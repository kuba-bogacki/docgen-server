package com.document.controller;

import com.document.model.dto.EvidenceDetailsDto;
import com.document.model.dto.FinancialStatementDto;

public class Samples {

    final String evidenceId = "dc827921-33c7-47e7-876f-e64ed02aaa32";
    final String companyId = "61b9be80-63d6-4ec0-9e99-934f6ab81bed";
    final String userEmail = "james.carter@yahoo.com";
    final String evidenceName = "Financial statement";
    final String evidenceType = "FINANCIAL_STATEMENT";
    final String createDateTime = "03-04-1995 11:44:56";
    final String periodStartDate = "03-04-1995";
    final String periodEndDate = "03-06-1997";
    final String managementBoardPresident = "James Carter";
    final String supervisoryBoardChairman = "Abdul Haram";
    final Float companyEquity = 123.12F;
    final Float companyTotalSum = 123.12F;
    final Float companyNetProfit = 123.12F;
    final Float companyNetIncrease = 123.12F;
    final byte[] evidenceContent = new byte[] {0, 0, 7};

    final EvidenceDetailsDto evidenceDetailsDto = EvidenceDetailsDto.builder()
            .evidenceId(evidenceId)
            .evidenceName(evidenceName)
            .evidenceType(evidenceType)
            .companyId(companyId)
            .createDateTime(createDateTime)
            .build();

    final FinancialStatementDto financialStatementDto = FinancialStatementDto.builder()
            .companyId(companyId)
            .periodStartDate(periodStartDate)
            .periodEndDate(periodEndDate)
            .companyEquity(companyEquity)
            .companyTotalSum(companyTotalSum)
            .companyNetProfit(companyNetProfit)
            .companyNetIncrease(companyNetIncrease)
            .managementBoardPresident(managementBoardPresident)
            .supervisoryBoardChairman(supervisoryBoardChairman)
            .build();
}

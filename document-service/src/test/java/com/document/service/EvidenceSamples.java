package com.document.service;

import com.document.model.Evidence;
import com.document.model.type.EvidenceType;

import static com.document.model.type.EvidenceType.FINANCIAL_STATEMENT;

public class EvidenceSamples {

    final String evidenceIdNo1 = "28df7ad6-c0d4-4310-b4f1-4ede2d728495";
    final String companyIdNo1 = "90a8a7ff-bac0-4f6f-9f27-0464b42f21ee";
    final String evidenceName = "Financial_statement.pdf";
    final byte[] evidenceContent = {10, 20, 30, 40, 50, -128, 0, 127};

    Evidence evidenceEntity = Evidence.builder()
            .evidenceType(FINANCIAL_STATEMENT)
            .evidenceName(evidenceName)
            .companyId(companyIdNo1)
            .evidenceContent(evidenceContent)
            .build();
}

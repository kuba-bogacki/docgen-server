package com.document.service;

import com.document.model.dto.EvidenceDetailsDto;
import com.document.model.dto.EvidenceDto;
import com.document.model.dto.FinancialStatementDto;

import java.util.List;

public interface EvidenceService {

    void createFinancialStatement(FinancialStatementDto financialStatementDto, String jwtToken);
    EvidenceDetailsDto getEvidenceDetailsById(String evidenceId);
    List<EvidenceDto> getAllCompanyEvidences(String companyId);
}

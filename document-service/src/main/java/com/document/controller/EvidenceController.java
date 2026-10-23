package com.document.controller;

import com.document.model.dto.EvidenceDetailsDto;
import com.document.model.dto.EvidenceDto;
import com.document.model.dto.FinancialStatementDto;
import com.document.service.EvidenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.document.util.ApplicationConstants.API_VERSION;
import static com.document.util.ApplicationConstants.USER_EMAIL_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/document")
public class EvidenceController {

    private final EvidenceService evidenceService;

    @GetMapping(value = "/{evidenceId}")
    public ResponseEntity<EvidenceDetailsDto> getEvidenceDetailsById(@PathVariable String evidenceId) {
        return new ResponseEntity<>(evidenceService.getEvidenceDetailsById(evidenceId), HttpStatus.OK);
    }

    @PostMapping(value = "/create-financial-statement")
    public ResponseEntity<Void> createFinancialStatement(@RequestHeader(USER_EMAIL_HEADER) String userEmail, @Valid @RequestBody FinancialStatementDto financialStatementDto) {
        evidenceService.createFinancialStatement(financialStatementDto, userEmail);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/get-all/{companyId}")
    public ResponseEntity<List<EvidenceDto>> getAllCompanyEvidences(@PathVariable String companyId) {
        return new ResponseEntity<>(evidenceService.getAllCompanyEvidences(companyId), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{evidenceId}")
    public ResponseEntity<Void> deleteEvidenceById(@PathVariable String evidenceId) {
        evidenceService.deleteEvidenceById(evidenceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

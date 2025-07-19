package com.document.controller;

import com.document.model.dto.FinancialStatementDto;
import com.document.service.EvidenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.document.util.ApplicationConstants.API_VERSION;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/document")
public class EvidenceController {

    private final EvidenceService evidenceService;

    @GetMapping(value = "/{evidenceId}")
    public ResponseEntity<?> getEvidenceDetailsById(@PathVariable String evidenceId) {
        return new ResponseEntity<>(evidenceService.getEvidenceDetailsById(evidenceId), HttpStatus.OK);
    }

    @PostMapping(value = "/create-financial-statement")
    public ResponseEntity<?> createFinancialStatement(@Valid @RequestBody FinancialStatementDto financialStatementDto, @RequestHeader("Authorization") String jwtToken) {
        evidenceService.createFinancialStatement(financialStatementDto, jwtToken);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/get-all/{companyId}")
    public ResponseEntity<?> getAllCompanyEvidences(@PathVariable String companyId) {
        return new ResponseEntity<>(evidenceService.getAllCompanyEvidences(companyId), HttpStatus.OK);
    }
}

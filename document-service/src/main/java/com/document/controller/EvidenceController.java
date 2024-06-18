package com.document.controller;

import com.document.model.dto.EvidenceDto;
import com.document.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.List;

import static com.document.util.ApplicationConstants.API_VERSION;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/document")
public class EvidenceController {

    private final EvidenceService evidenceService;

    @GetMapping(value = "/{evidenceName}")
    public ResponseEntity<?> getEvidenceByName(@PathVariable String evidenceName) {
        EvidenceDto evidenceDto = evidenceService.getEvidenceByName(evidenceName);
        return new ResponseEntity<>(evidenceDto, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> createEvidence(@RequestBody EvidenceDto evidenceDto) {
        try {
            evidenceService.createEvidence(evidenceDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (WebClientException e) {
            return new ResponseEntity<>("Could not save evidence", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/get-all")
    public ResponseEntity<?> getAllEvidences() {
        List<EvidenceDto> evidenceList = evidenceService.getAllEvidences();
        return new ResponseEntity<>(evidenceList, HttpStatus.OK);
    }
}

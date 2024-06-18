package com.document.service;

import com.document.model.dto.EvidenceDto;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.List;

public interface EvidenceService {

    void createEvidence(EvidenceDto evidenceDto) throws WebClientException;
    EvidenceDto getEvidenceByName(String evidenceName);
    List<EvidenceDto> getAllEvidences();
}

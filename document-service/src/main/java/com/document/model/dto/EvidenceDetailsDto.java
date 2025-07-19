package com.document.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvidenceDetailsDto {

    private String evidenceId;
    private String evidenceName;
    private String evidenceType;
    private String companyId;
    private String createDateTime;
    private byte[] evidenceContent;
}

package com.document.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceDto {

    private String evidenceId;
    private String evidenceName;
    private String companyId;
    private String createDateTime;
}

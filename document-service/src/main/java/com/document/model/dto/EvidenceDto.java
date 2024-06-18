package com.document.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EvidenceDto {

    private String evidenceId;
    private String evidenceName;
    private String companyId;
}

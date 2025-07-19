package com.document.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EvidenceType {

    FINANCIAL_STATEMENT("Financial statement");

    private final String description;
}

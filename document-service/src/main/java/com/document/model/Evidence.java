package com.document.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Document(value = "evidence")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Evidence {

    @MongoId(targetType = FieldType.STRING)
    private String evidenceId;
    private String evidenceName;
    private String companyId;
}

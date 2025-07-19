package com.document.model;

import com.document.model.type.EvidenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Builder
@Document(value = "evidence")
@AllArgsConstructor
@NoArgsConstructor
public class Evidence {

    @MongoId(targetType = FieldType.STRING)
    private String evidenceId;

    private String evidenceName;

    private EvidenceType evidenceType;

    private String companyId;

    private Timestamp createDateTime;

    private byte[] evidenceContent;

    @Builder(builderMethodName = "internalBuilder")
    public static Evidence buildEvidence(EvidenceType type, String evidenceName, String companyId, byte[] evidenceContent) {
        if (type == null || evidenceName == null) {
            throw new IllegalArgumentException("Evidence type and evidence name cannot be null");
        }
        var evidenceFullName = String.format("%s_%s", type.getDescription(), evidenceName).replaceAll(" ", "_");

        return new Evidence(null, evidenceFullName, type, companyId, Timestamp.from(Instant.now()), evidenceContent);
    }

    public static EvidenceBuilder builder() {
        return internalBuilder();
    }
}

package com.document.model;

import com.document.model.type.EvidenceType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.sql.Timestamp;

import static java.util.Objects.requireNonNull;

@Data
@Document(value = "evidence")
@NoArgsConstructor
public class Evidence {

    @MongoId(targetType = FieldType.STRING)
    private String evidenceId;

    private EvidenceType evidenceType;

    private String evidenceName;

    private String companyId;

    @CreatedDate
    private Timestamp createDateTime;

    private byte[] evidenceContent;

    public static Builder builder() {
        return new Builder();
    }

    public Evidence(Builder builder) {
        this.evidenceName = builder.evidenceName;
        this.evidenceType = builder.evidenceType;
        this.companyId = builder.companyId;
        this.evidenceContent = builder.evidenceContent;
    }

    public static class Builder {

        private EvidenceType evidenceType;
        private String evidenceName;
        private String companyId;
        private byte[] evidenceContent;

        public Builder evidenceType(EvidenceType evidenceType) {
            requireNonNull(evidenceType, "Evidence type cannot be null");
            this.evidenceType = evidenceType;
            return this;
        }

        public Builder evidenceName(String evidenceName) {
            requireNonNull(evidenceName, "Evidence name cannot be null");
            this.evidenceName = String.format("%s_%s", this.evidenceType.getDescription(), evidenceName).replaceAll(" ", "_");
            return this;
        }

        public Builder companyId(String companyId) {
            requireNonNull(companyId, "Company Id cannot be null");
            this.companyId = companyId;
            return this;
        }

        public Builder evidenceContent(byte[] evidenceContent) {
            requireNonNull(evidenceContent, "Evidence content cannot be null");
            this.evidenceContent = evidenceContent;
            return this;
        }

        public Evidence build() {
            return new Evidence(this);
        }
    }
}

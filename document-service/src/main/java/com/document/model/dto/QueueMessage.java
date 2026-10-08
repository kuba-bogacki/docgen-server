package com.document.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class QueueMessage {

    private UUID messageId;
    private Instant occurredAt;
    private String evidenceId;
    private String userEmail;
    private String evidenceName;
    private String templateFileName;
    private Map<String, String> placeholders;
}

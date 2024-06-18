package com.event.model.dto;

import com.event.model.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    private UUID eventId;
    private EventType eventType;
    private String eventDate;
    private String eventTime;
    private Boolean eventHeld;
}

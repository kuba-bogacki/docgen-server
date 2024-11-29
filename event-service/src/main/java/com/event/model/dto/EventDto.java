package com.event.model.dto;

import com.event.model.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    private String eventId;
    private String eventCompany;
    private String eventTitle;
    private String eventDate;
    private String eventTime;
    private EventType eventType;
    private Set<String> eventMembers = new HashSet<>();
}

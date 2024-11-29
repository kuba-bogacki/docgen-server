package com.event.service;

import com.event.model.Event;
import com.event.model.dto.EventDto;
import com.event.model.type.EventType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Set;
import java.util.UUID;

class EventSamples {

    final UUID eventIdI = UUID.fromString("f5e6995e-8fa4-4dfa-bf83-f7553e6e6596");
    final UUID eventIdII = UUID.fromString("63dba352-d540-48d7-8ef7-859e87acf773");
    final UUID eventCompanyI = UUID.fromString("2b93636c-a257-4e95-bbe0-a0ed4c3c4072");
    final String eventTitleI = "Company meeting";
    final String eventTitleII = "Company weekly";
    final LocalDate eventDate = LocalDate.of(2023, Month.DECEMBER, 12);
    final LocalTime eventTimeI = LocalTime.of(23, 0, 0);
    final LocalTime eventTimeII = LocalTime.of(12, 30, 0);
    final EventType eventTypeI = EventType.MEETING;
    final EventType eventTypeII = EventType.VOTING;
    final Set<UUID> eventMembers = Set.of(UUID.fromString("0a251bc5-69ba-4d38-849c-e551a1c9e57a"), UUID.fromString("41582331-541d-482c-8f5b-05fc76666711"));
    final String eventDateString = "12-12-2023";
    final String eventTimeStringI = "23:00";
    final String eventTimeStringII = "12:30";
    final Set<String> eventMembersString = Set.of("0a251bc5-69ba-4d38-849c-e551a1c9e57a", "41582331-541d-482c-8f5b-05fc76666711");

    EventDto sampleEventDtoI = EventDto.builder()
            .eventCompany(eventCompanyI.toString())
            .eventTitle(eventTitleI)
            .eventDate(eventDateString)
            .eventTime(eventTimeStringI)
            .eventType(eventTypeI)
            .eventMembers(eventMembersString)
            .build();

    EventDto sampleEventDtoII = EventDto.builder()
            .eventCompany(eventCompanyI.toString())
            .eventTitle(eventTitleII)
            .eventDate(eventDateString)
            .eventTime(eventTimeStringII)
            .eventType(eventTypeII)
            .eventMembers(eventMembersString)
            .build();

    Event sampleEventEntityI = Event.builder()
            .eventCompany(eventCompanyI)
            .eventTitle(eventTitleI)
            .eventDate(eventDate)
            .eventTime(eventTimeI)
            .eventType(eventTypeI)
            .eventMembers(eventMembers)
            .build();

    Event sampleEventEntityII = Event.builder()
            .eventCompany(eventCompanyI)
            .eventTitle(eventTitleII)
            .eventDate(eventDate)
            .eventTime(eventTimeII)
            .eventType(eventTypeII)
            .eventMembers(eventMembers)
            .build();
}

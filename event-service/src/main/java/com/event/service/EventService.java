package com.event.service;

import com.event.model.dto.EventDto;

import java.util.List;
import java.util.UUID;

public interface EventService {

    EventDto getEventByEventId(UUID eventId);
    List<EventDto> getAllEvents();
    void createEvent(EventDto eventDto);
    boolean votingEventTookPlace();
}

package com.event.service;

import com.event.model.dto.EventDto;

import java.util.List;

public interface EventService {
    List<EventDto> getCompanyEventsByDate(String companyId, String date);
    List<EventDto> getCompanyEvents(String companyId);
    EventDto createEvent(EventDto eventDto);
    void deleteCompanyEvent(String eventId);
}

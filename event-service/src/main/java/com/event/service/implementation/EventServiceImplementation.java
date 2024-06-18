package com.event.service.implementation;

import com.event.mapper.EventMapper;
import com.event.model.Event;
import com.event.model.dto.EventDto;
import com.event.repository.EventRepository;
import com.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.event.model.type.EventType.VOTING;

@Service
@RequiredArgsConstructor
@Log4j2
public class EventServiceImplementation implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public EventDto getEventByEventId(UUID eventId) {
        return eventMapper.mapToDto(eventRepository.findEventByEventId(eventId));
    }

    @Override
    public void createEvent(EventDto eventDto) {
        Event event = eventRepository.save(eventMapper.mapToEntity(eventDto));
        log.info("Event has been created with id : {}", event.getEventId());
    }

    @Override
    public List<EventDto> getAllEvents() {
        return eventMapper.mapToDtos(eventRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean votingEventTookPlace() {
        return eventRepository.findAll().stream()
                .anyMatch(event -> event.getEventType() == VOTING);
    }
}

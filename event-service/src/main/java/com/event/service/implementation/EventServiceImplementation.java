package com.event.service.implementation;

import com.event.exception.EventNotExistException;
import com.event.mapper.EventMapper;
import com.event.model.Event;
import com.event.model.dto.EventDto;
import com.event.repository.EventRepository;
import com.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImplementation implements EventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getCompanyEventsByDate(String companyId, String date) {
        final var sortedEventsList = eventRepository.findAllByEventCompanyAndEventDate(UUID.fromString(companyId), parseCustomLocalDate(date)).stream()
                .sorted(Comparator.comparing(Event::getEventTime))
                .toList();
        return eventMapper.mapToDtos(sortedEventsList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getCompanyEvents(String companyId) {
        return eventMapper.mapToDtos(eventRepository.findAllByEventCompany(UUID.fromString(companyId)));
    }

    @Override
    @Transactional
    public EventDto createEvent(EventDto eventDto) {
        final var event = eventRepository.save(eventMapper.mapToEntity(eventDto));
        log.info("Event has been created with id : {}", event.getEventId());
        return eventMapper.mapToDto(event);
    }

    @Override
    @Transactional
    public void deleteCompanyEvent(String eventId) {
        final var optionalEvent = eventRepository.findById(UUID.fromString(eventId));
        if (optionalEvent.isEmpty()) {
            throw new EventNotExistException(String.format("Event with id [%s] does not exist", eventId));
        }
        eventRepository.deleteById(UUID.fromString(eventId));
        log.info("Event with id {} has been successfully deleted.", eventId);
    }

    private LocalDate parseCustomLocalDate(String date) {
        final var dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, dateFormatter);
    }
}

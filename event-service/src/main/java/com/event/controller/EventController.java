package com.event.controller;

import com.event.model.dto.EventDto;
import com.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.event.util.ApplicationConstants.API_VERSION;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/event")
public class EventController {

    private final EventService eventService;

    @GetMapping(value = "/{eventId}")
    public ResponseEntity<?> getEventByEventId(@PathVariable String eventId) {
        EventDto event = eventService.getEventByEventId(UUID.fromString(eventId));
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> createEvent(@RequestBody EventDto eventDto) {
        eventService.createEvent(eventDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/get-all")
    public ResponseEntity<?> getAllEvents() {
        List<EventDto> events = eventService.getAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping(value = "/voting-held")
    public ResponseEntity<?> votingEventTookPlace() {
        Boolean votingHeld = eventService.votingEventTookPlace();
        return new ResponseEntity<>(votingHeld, HttpStatus.OK);
    }
}

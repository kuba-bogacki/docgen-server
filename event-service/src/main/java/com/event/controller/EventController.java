package com.event.controller;

import com.event.model.dto.EventDto;
import com.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.event.util.ApplicationConstants.API_VERSION;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/event")
public class EventController {

    private final EventService eventService;

    @GetMapping(value = "/get/{companyId}/{date}")
    public ResponseEntity<List<EventDto>> getCompanyEventsByDate(@PathVariable String companyId, @PathVariable String date) {
        return new ResponseEntity<>(eventService.getCompanyEventsByDate(companyId, date), HttpStatus.OK);
    }

    @GetMapping(value = "/get-all/{companyId}")
    public ResponseEntity<List<EventDto>> getCompanyEvents(@PathVariable String companyId) {
        return new ResponseEntity<>(eventService.getCompanyEvents(companyId), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto) {
        return new ResponseEntity<>(eventService.createEvent(eventDto), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/delete/{eventId}")
    public ResponseEntity<Void> deleteCompanyEvent(@PathVariable String eventId) {
        eventService.deleteCompanyEvent(eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

package com.event.controller;

import com.event.model.dto.EventDto;
import com.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.event.util.ApplicationConstants.API_VERSION;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/event")
public class EventController {

    private final EventService eventService;

    @GetMapping(value = "/get/{companyId}/{date}")
    public ResponseEntity<?> getCompanyEventsByDate(@PathVariable("companyId") String companyId, @PathVariable("date") String date) {
        try {
            return new ResponseEntity<>(eventService.getCompanyEventsByDate(companyId, date), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/get-all/{companyId}")
    public ResponseEntity<?> getCompanyEvents(@PathVariable String companyId) {
        try {
            return new ResponseEntity<>(eventService.getCompanyEvents(companyId), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> createEvent(@RequestBody EventDto eventDto) {
        try {
            return new ResponseEntity<>(eventService.createEvent(eventDto), HttpStatus.CREATED);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @DeleteMapping(value = "/delete/{eventId}")
    public ResponseEntity<?> deleteCompanyEvent(@PathVariable("eventId") String eventId) {
        try {
            eventService.deleteCompanyEvent(eventId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

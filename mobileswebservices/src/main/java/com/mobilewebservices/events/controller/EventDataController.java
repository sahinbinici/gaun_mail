package com.mobilewebservices.events.controller;

import com.mobilewebservices.events.dto.EventDataDto;
import com.mobilewebservices.events.service.EventDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventDataController {

    private final EventDataService eventDataService;

    public EventDataController(EventDataService eventDataService) {
        this.eventDataService = eventDataService;
    }

    @GetMapping
    public ResponseEntity<List<EventDataDto>> getAllEvents() {
        return ResponseEntity.ok(eventDataService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDataDto> getEventById(@PathVariable Integer id) {
        return ResponseEntity.ok(eventDataService.getEventById(id));
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<EventDataDto>> getEventsByYear(@PathVariable Integer year) {
        return ResponseEntity.ok(eventDataService.getEventsByYear(year));
    }

    @GetMapping("/sender/{sender}")
    public ResponseEntity<List<EventDataDto>> getEventsBySender(@PathVariable String sender) {
        return ResponseEntity.ok(eventDataService.getEventsBySender(sender));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<EventDataDto>> getEventsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(eventDataService.getEventsByDateRange(startDate, endDate));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<EventDataDto>> getLatest10Events() {
        return ResponseEntity.ok(eventDataService.getLatest10Events());
    }

    @GetMapping("/last-month")
    public ResponseEntity<List<EventDataDto>> getEventsFromLastMonth() {
        return ResponseEntity.ok(eventDataService.getEventsFromLastMonth());
    }
}

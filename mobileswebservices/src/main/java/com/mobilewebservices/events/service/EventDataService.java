package com.mobilewebservices.events.service;

import com.mobilewebservices.events.dto.EventDataDto;
import com.mobilewebservices.events.repository.EventDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventDataService {

    private final EventDataRepository eventDataRepository;

    public EventDataService(EventDataRepository eventDataRepository) {
        this.eventDataRepository = eventDataRepository;
    }

    public List<EventDataDto> getAllEvents() {
        return eventDataRepository.findAll();
    }

    public EventDataDto getEventById(Integer id) {
        return eventDataRepository.findById(id);
    }

    public List<EventDataDto> getEventsByYear(Integer year) {
        return eventDataRepository.findByYear(year);
    }

    public List<EventDataDto> getEventsBySender(String sender) {
        return eventDataRepository.findBySender(sender);
    }

    public List<EventDataDto> getEventsByDateRange(String startDate, String endDate) {
        return eventDataRepository.findByDateRange(startDate, endDate);
    }

    public List<EventDataDto> getLatest10Events() {
        return eventDataRepository.findLatest10Events();
    }

    public List<EventDataDto> getEventsFromLastMonth() {
        return eventDataRepository.findEventsFromLastMonth();
    }
}

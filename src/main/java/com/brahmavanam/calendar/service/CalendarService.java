package com.brahmavanam.calendar.service;

import com.brahmavanam.calendar.model.Event;
import com.brahmavanam.calendar.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class CalendarService {

    @Autowired
    EventRepository eventRepository;

    public List<Event> getEventDetails() {

        return new ArrayList<>();
    }

    public Event saveEventDetails(Event event) {

        // Parse the start date of the new event
        LocalDateTime eventStartDate = LocalDateTime.parse(event.getStartDate().substring(0, 23), DateTimeFormatter.ISO_DATE_TIME);


        if(eventStartDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event start date cannot be in the past.");
        }

        // Fetch all events created by the user
        List<Event> userEvents = eventRepository.findByUserId(event.getUser().getId());
        log.info("User events: " + userEvents);

        // Check if the new event is at least 90 days apart from all existing events
        for (Event existingEvent : userEvents) {
            LocalDateTime existingEventStartDate = LocalDateTime.parse(existingEvent.getStartDate().substring(0, 23), DateTimeFormatter.ISO_DATE_TIME);
            if (eventStartDate.isBefore(existingEventStartDate.plusDays(90)) && eventStartDate.isAfter(existingEventStartDate.minusDays(90))) {
                throw new IllegalArgumentException("User must wait at least 90 days between events.");
            }
        }

        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {

        List<Event> events = eventRepository.findAll();
        log.info("Get all Events: " + events);
        return events;
    }

    public boolean deleteEvent(Long eventId, int userDTOId) {
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null) {
            throw new IllegalArgumentException("Event not found.");
        }


        if(LocalDateTime.parse(event.getStartDate().substring(0, 23), DateTimeFormatter.ISO_DATE_TIME).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("An event of past belongs to past. Can neither be modified nor deleted.");
        }

        if (event != null && event.getUser().getId() == userDTOId) {
            log.info("CalendarService: Deleting event with id: {}", eventId);
            eventRepository.delete(event);
            return true;
        }
        log.info("CalendarService: Deleting event with id: {} failed", eventId);
        return false;
    }
}
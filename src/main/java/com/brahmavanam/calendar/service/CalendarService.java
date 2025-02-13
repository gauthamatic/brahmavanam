package com.brahmavanam.calendar.service;

import com.brahmavanam.calendar.model.Event;
import com.brahmavanam.calendar.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        // Parse the start date & end date of the new event
        LocalDate eventStartDate = LocalDate.parse(event.getStartDate().substring(0, 23), DateTimeFormatter.ISO_DATE_TIME);
        LocalDate eventEndDate = LocalDate.parse(event.getEndDate().substring(0, 23), DateTimeFormatter.ISO_DATE_TIME);

        System.out.println("Event Start Date: " + eventStartDate + " Event End Date: " + eventEndDate);

        // Check if the start date is for a future date
        if(eventStartDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Those who live in the past are not encouraged at Brahmavanam. Look ahead and select a future date.");
        }

        //Fetch all events for eventStartDate
        List<Event> eventsForSelectedDate = eventRepository.findByStartDate(event.getStartDate());
        log.info("Events: " + eventsForSelectedDate);

        // Check if there is already an event scheduled for the selected date
        if(eventsForSelectedDate.size() > 0) {
            throw new IllegalArgumentException("Truth is Non-dual!! A Booking in Brahmavanam should also be! Please select a date where there is no booking scheduled.");
        }

        // Fetch all events created by the user
        List<Event> userEvents = eventRepository.findByUserId(event.getUser().getId());
        log.info("User events: " + userEvents);

        for (Event existingEvent : userEvents) {
            LocalDate existingEventStartDate = LocalDate.parse(existingEvent.getStartDate().substring(0, 23), DateTimeFormatter.ISO_DATE_TIME);

            // Check if there is already an active event
            if (existingEventStartDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Active booking found for you!! While enthusiasm is great, there may be others who are as enthusiastic as you. Comeback after completing the active booking!!!");
            }

            // Check if the new event is at least 90 days apart from all existing events
            if (eventStartDate.isBefore(existingEventStartDate.plusDays(90))) {
                throw new IllegalArgumentException("Meditate; deepen your practices & wait for at least 90 days between events.");
            }

        }

        // Check if the event is for two days and not for only one day
        if(eventStartDate.equals(eventEndDate.minusDays(1))) {
            throw new IllegalArgumentException("A day is not enough to experience the bliss of Brahmavanam. Book for two days or else come on Sunday.");
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


        if(LocalDate.parse(event.getStartDate().substring(0, 23), DateTimeFormatter.ISO_DATE_TIME).isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("An event of past belongs to past. It can neither be modified nor deleted.");
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
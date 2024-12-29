package com.brahmavanam.calendar.service;

import com.brahmavanam.calendar.model.Event;
import com.brahmavanam.calendar.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {

    @Autowired
    EventRepository eventRepository;

    public List<Event> getEventDetails() {

        return new ArrayList<>();
    }

    public Event saveEventDetails(Event event) {

        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {

        List<Event> events = eventRepository.findAll();
        System.out.println("Events: " + events);
        return events;
    }
}
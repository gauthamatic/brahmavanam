package com.brahmavanam.calendar.service;

import com.brahmavanam.calendar.model.Event;
import com.brahmavanam.calendar.model.RRule;
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

    public void saveEventDetails() {

    }

    public List<Event> getAllEvents() {

//        List<Event> events = new ArrayList<>();
//        events.add(new Event(){
//            {
//                setTitle("Open to All");
//                setRrule(new RRule(){
//                    {
//                        setFreq("weekly");
//                        setInterval(1);
//                        setByweekday(new String[]{"su"});
//                        setDtstart("2023-01-01T10:00:00");
//                    }
//                });
//                setColor("#ffcccc");
//                setTextColor("#990000");
//            }
//        });
        return eventRepository.findAll();
    }
}
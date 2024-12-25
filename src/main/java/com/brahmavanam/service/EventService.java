package com.brahmavanam.service;

import com.brahmavanam.model.Event;
import com.brahmavanam.model.RRule;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {
    public List<Event> getEventDetails() {
        return new ArrayList<>();
    }

    public void saveEventDetails() {

    }

    public List<Event> getAllEvents() {

        List<Event> events = new ArrayList<>();
        events.add(new Event(){
            {
                setTitle("Open to All");
                setRrule(new RRule(){
                    {
                        setFreq("weekly");
                        setInterval(1);
                        setByweekday(new String[]{"su"});
                        setDtstart("2023-01-01T10:00:00");
                    }
                });
                setColor("#ffcccc");
                setTextColor("#990000");
            }
        });
        return events;
    }
}
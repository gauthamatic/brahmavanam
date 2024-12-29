package com.brahmavanam.controller;

import com.brahmavanam.calendar.dto.EventDTO;
import com.brahmavanam.calendar.dto.RRuleDTO;
import com.brahmavanam.calendar.model.Event;
import com.brahmavanam.calendar.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class Router {

    @Autowired
    CalendarService calendarService;

    @GetMapping("/namana")
    public RedirectView namana(){
        String wordpressUrl = "https://brahmavanam.wordpress.com/%E0%B2%A8%E0%B2%AE%E0%B2%A8/";
        return new RedirectView(wordpressUrl);
    }

    @GetMapping("/calendar")
    public String calendar(){

        return "calendarHome";
    }

    @GetMapping("/events")
    @ResponseBody
    public List<EventDTO> events(){

        return calendarService.getAllEvents().
                stream().
                map(this::convertToDTO).
                collect(Collectors.toList());
    }

    @PostMapping("/events")
    @ResponseBody
    public EventDTO saveEvents(@RequestBody EventDTO eventDTO){

        System.out.println("Request EventDTO: " + eventDTO);
        return convertToDTO(calendarService.saveEventDetails(convertToEntity(eventDTO)));
    }

    private EventDTO convertToDTO(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setTitle(event.getTitle());
        eventDTO.setStart(event.getDtstart());
        eventDTO.setEnd(event.getDtend());
        eventDTO.setColor(event.getColor());
        eventDTO.setTextColor(event.getTextColor());

        if (event.getRrule() != null) {
            RRuleDTO rruleDTO = new RRuleDTO();
            rruleDTO.setFreq(event.getRrule().getFreq());
            rruleDTO.setInterval(event.getRrule().getInterval());
            rruleDTO.setByweekday(event.getRrule().getByweekday().split(","));
            rruleDTO.setDtstart(event.getRrule().getDtstart());
            eventDTO.setRrule(rruleDTO);
        }
        System.out.println("Response EventDTO: " + eventDTO);
        return eventDTO;
    }

    private Event convertToEntity(EventDTO eventDTO) {
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDtstart(eventDTO.getStart());
        event.setDtend(eventDTO.getEnd());
        event.setColor(eventDTO.getColor());
        event.setTextColor(eventDTO.getTextColor());

        System.out.println("Event: " + event);
        return event;
    }

}
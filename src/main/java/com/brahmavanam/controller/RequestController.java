package com.brahmavanam.controller;

import com.brahmavanam.model.Event;
import com.brahmavanam.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class RequestController {

    @Autowired
    EventService eventService;

    @GetMapping("/namana")
    public RedirectView namana(){
        String wordpressUrl = "https://brahmavanam.wordpress.com/%E0%B2%A8%E0%B2%AE%E0%B2%A8/"; // Replace with your WordPress page URL
        return new RedirectView(wordpressUrl);
    }

    @GetMapping("/calendar")
    public String calendar(){
        return "calendarHome";
    }

    @GetMapping("/events")
    @ResponseBody
    public List<Event> events(){
        return eventService.getAllEvents();
    }

    @PostMapping("/events")
    public void saveEvents(){
        eventService.saveEventDetails();
    }

}
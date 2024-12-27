package com.brahmavanam.controller;

import com.brahmavanam.calendar.model.Event;
import com.brahmavanam.calendar.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

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
    public List<Event> events(){
        return calendarService.getAllEvents();
    }

    @PostMapping("/events")
    public void saveEvents(){
        calendarService.saveEventDetails();
    }

}
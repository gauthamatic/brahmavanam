package com.brahmavanam.controller;

import com.brahmavanam.calendar.dto.EventDTO;
import com.brahmavanam.calendar.dto.RRuleDTO;
import com.brahmavanam.calendar.dto.UserDTO;
import com.brahmavanam.calendar.model.Event;
import com.brahmavanam.calendar.model.RRule;
import com.brahmavanam.calendar.model.User;
import com.brahmavanam.calendar.service.CalendarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class Router {

    @Autowired
    CalendarService calendarService;

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        String userName = (String) session.getAttribute("user"); // Get user name from session
        if (userName != null) {
            model.addAttribute("userName", userName);
        }
        return "home"; // Return home page
    }

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
        eventDTO.setStart(event.getStartDate().toString());
        eventDTO.setEnd(event.getEndDate().toString());
        eventDTO.setColor(event.getColor());
        eventDTO.setTextColor(event.getTextColor());

        RRule rrule = event.getRrule();
        if (rrule != null) {
            RRuleDTO rruleDTO = new RRuleDTO();
            rruleDTO.setFreq(rrule.getFreq());
            rruleDTO.setInterval(rrule.getInterval());
            rruleDTO.setByweekday(rrule.getByweekday().split(","));
            rruleDTO.setDtstart(rrule.getDtstart());
            eventDTO.setRrule(rruleDTO);
        }

        User user = event.getUser();
        if (user != null){
            UserDTO userDTO = new UserDTO();
            userDTO.setFirstName(user.getFirstname());
            userDTO.setLastName(user.getLastname());
            userDTO.setEmailId(user.getEmailId());
            userDTO.setPassword(user.getPassword());
            eventDTO.setUser(userDTO);
        }

        System.out.println("Response EventDTO: " + eventDTO);
        return eventDTO;
    }

    private Event convertToEntity(EventDTO eventDTO) {
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setStartDate(LocalDate.parse(eventDTO.getStart().substring(0, 10)));
        event.setEndDate(LocalDate.parse(eventDTO.getEnd().substring(0, 10)));
        event.setColor(eventDTO.getColor());
        event.setTextColor(eventDTO.getTextColor());

        System.out.println("Event: " + event);
        return event;
    }

}
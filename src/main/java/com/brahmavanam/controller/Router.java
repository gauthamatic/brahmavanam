package com.brahmavanam.controller;

import com.brahmavanam.calendar.dto.EventDTO;
import com.brahmavanam.calendar.dto.RRuleDTO;
import com.brahmavanam.calendar.dto.UserDTO;
import com.brahmavanam.calendar.model.Event;
import com.brahmavanam.calendar.model.RRule;
import com.brahmavanam.calendar.model.User;
import com.brahmavanam.calendar.service.CalendarService;
import com.brahmavanam.calendar.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class Router {

    @Autowired
    CalendarService calendarService;

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        UserDTO userDTO = getUser(session);
        String userName = userDTO != null ? userDTO.getFirstName() : null;
        if (userName != null) {
        model.addAttribute("userName", userName);
        }
        return "home";
    }
    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        UserDTO userDTO = getUser(session);
        String userName = userDTO != null ? userDTO.getFirstName() : null;
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
    public String calendar(@RequestParam(required = false) String date, HttpSession session, Model model){
        UserDTO userDTO = getUser(session);
        String userName = userDTO != null ? userDTO.getFirstName() : null;
        if (userName != null) {
            model.addAttribute("userName", userName);
        }
        model.addAttribute("date", date);
        log.info("Router: Calendar date: " + date);
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

        log.info("Saving Event --> DTO: " + eventDTO);
        EventDTO savedEventDTO = convertToDTO(calendarService.saveEventDetails(convertToEntity(eventDTO)));
        log.info("Saved Event --> DTO: " + savedEventDTO);
        return savedEventDTO;
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id, HttpSession session){
        UserDTO userDTO = getUser(session);
        if(userDTO == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("Router: Deleting event with id: {}", id);
        boolean deleted = calendarService.deleteEvent(Long.parseLong(id), userDTO.getId());
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/user")
    @ResponseBody
    public UserDTO getUser(HttpSession session){
        String userName = (String) session.getAttribute("user"); // Get user name from session
        if (userName == null) {
            return null; // or handle the case where the user is not found in the session
        }
        User user = userService.findUserByUsername(userName); // Assuming you have a method to find user by username
        if (user == null) {
            return null; // or handle the case where the user is not found in the database
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstname());
        userDTO.setLastName(user.getLastname());
        userDTO.setEmailId(user.getEmailId());
        return userDTO;
    }

    private EventDTO convertToDTO(Event event) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(String.valueOf(event.getId()));
        eventDTO.setTitle(event.getTitle());
        eventDTO.setStart(event.getStartDate());
        eventDTO.setEnd(event.getEndDate());
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
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstname());
            userDTO.setLastName(user.getLastname());
            userDTO.setEmailId(user.getEmailId());
            userDTO.setPassword(user.getPassword());
            eventDTO.setUser(userDTO);
        }

        return eventDTO;
    }

    private Event convertToEntity(EventDTO eventDTO) {
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setStartDate(eventDTO.getStart());
        event.setEndDate(eventDTO.getEnd());
        event.setColor(eventDTO.getColor());
        event.setTextColor(eventDTO.getTextColor());
        event.setUser(userService.findUserById(eventDTO.getUser().getId()));

        return event;
    }

}
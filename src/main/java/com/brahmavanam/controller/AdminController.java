package com.brahmavanam.controller;

import com.brahmavanam.calendar.model.Event;
import com.brahmavanam.calendar.model.User;
import com.brahmavanam.calendar.repository.EventRepository;
import com.brahmavanam.calendar.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private static final String ADMIN_SESSION_KEY = "adminLoggedIn";

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Login page ──────────────────────────────────────────────────────────
    @GetMapping("")
    public String adminLoginPage(HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY))) {
            return "redirect:/admin/dashboard";
        }
        return "admin-login";
    }

    @PostMapping("/login")
    public String adminLogin(@RequestParam String username,
                             @RequestParam String password,
                             HttpSession session,
                             Model model) {
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            session.setAttribute(ADMIN_SESSION_KEY, true);
            log.info("Admin logged in");
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("error", "Invalid admin credentials");
        return "admin-login";
    }

    @GetMapping("/logout")
    public String adminLogout(HttpSession session) {
        session.removeAttribute(ADMIN_SESSION_KEY);
        return "redirect:/admin";
    }

    // ── Dashboard ────────────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin";
        }
        List<Event> events = eventRepository.findAll();
        List<User> users = userRepository.findAll();
        model.addAttribute("events", events);
        model.addAttribute("users", users);
        model.addAttribute("newEvent", new Event());
        return "admin-dashboard";
    }

    // ── Event: create / edit form submit ────────────────────────────────────
    @PostMapping("/event/save")
    public String saveEvent(@RequestParam(required = false) Long id,
                            @RequestParam String title,
                            @RequestParam String startDate,
                            @RequestParam String endDate,
                            @RequestParam(required = false) String color,
                            @RequestParam(required = false) String textColor,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin";
        }

        Event event;
        if (id != null) {
            event = eventRepository.findById(id).orElse(new Event());
        } else {
            event = new Event();
        }

        event.setTitle(title);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setColor(color != null && !color.isEmpty() ? color : "#ffcccc");
        event.setTextColor(textColor != null && !textColor.isEmpty() ? textColor : "#990000");

        eventRepository.save(event);
        log.info("Admin saved event: id={}, title={}", event.getId(), title);
        redirectAttributes.addFlashAttribute("success", "Event '" + title + "' saved successfully.");
        return "redirect:/admin/dashboard";
    }

    // ── Event: delete ────────────────────────────────────────────────────────
    @PostMapping("/event/delete/{id}")
    public String deleteEvent(@PathVariable Long id,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin";
        }
        eventRepository.findById(id).ifPresent(event -> {
            eventRepository.delete(event);
            log.info("Admin deleted event id={}", id);
        });
        redirectAttributes.addFlashAttribute("success", "Event deleted successfully.");
        return "redirect:/admin/dashboard";
    }

    // ── User: delete ─────────────────────────────────────────────────────────
    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Integer id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin";
        }
        userRepository.findById(id).ifPresent(user -> {
            userRepository.delete(user);
            log.info("Admin deleted user id={}, email={}", id, user.getEmailId());
        });
        redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        return "redirect:/admin/dashboard";
    }

    // ── Helper ───────────────────────────────────────────────────────────────
    private boolean isAdminLoggedIn(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY));
    }
}

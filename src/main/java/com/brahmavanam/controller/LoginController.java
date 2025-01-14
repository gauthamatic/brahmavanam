package com.brahmavanam.controller;

import com.brahmavanam.calendar.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        boolean isValid = userService.validateUser(email, password);
        if (isValid) {
            // Set user session
            session.setAttribute("user", email);
            return "redirect:/home"; // Redirect to dashboard or home page
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login"; // Redirect back to login page with an error message
        }
    }
}

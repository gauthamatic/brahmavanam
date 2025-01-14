package com.brahmavanam.controller;

import com.brahmavanam.calendar.model.User;
import com.brahmavanam.calendar.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Value("${master.password}")
    private String masterPassword;

    @GetMapping("/login")
    public String login(Model model){
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup"; // Return the signup.html page
    }

    @GetMapping("/forgotPassword")
    public String forgotPasswordPage() {
        return "forgot_password"; // Return the signup.html page
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

    @PostMapping("/signup")
    public String signup(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String masterpassword,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!masterpassword.equals(masterPassword)){
            model.addAttribute("error", "Master password is incorrect");
            return "signup"; // Show the sign-up page with an error
        }

        boolean userExists = userService.checkIfUserExists(email);

        if (userExists) {
            model.addAttribute("error", "Email already registered.");
            return "signup"; // Show the sign-up page with an error
        }

        // Create and save a new user
        User newUser = new User();
        newUser.setFirstname(firstname);
        newUser.setLastname(lastname);
        newUser.setEmailId(email);
        newUser.setPassword(password); // Store hashed password in real applications
        userService.saveUser(newUser);

        redirectAttributes.addFlashAttribute("success", "Account created successfully! Please log in");
        System.out.println("User saved successfully: " + newUser);
        return "redirect:/login"; // Redirect to login page after successful sign-up
    }
}

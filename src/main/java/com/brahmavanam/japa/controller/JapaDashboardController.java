package com.brahmavanam.japa.controller;

import com.brahmavanam.calendar.service.UserService;
import com.brahmavanam.japa.dto.JapaDashboardDTO;
import com.brahmavanam.japa.service.JapaDashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/japa/dashboard")
public class JapaDashboardController {

    @Autowired
    private JapaDashboardService japaDashboardService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<JapaDashboardDTO> getDashboard(
            @RequestParam(defaultValue = "7") int days,
            HttpSession session) {
        int userId = getAuthenticatedUserId(session);
        return ResponseEntity.ok(japaDashboardService.getDashboard(userId, days));
    }

    private int getAuthenticatedUserId(HttpSession session) {
        String email = (String) session.getAttribute("user");
        if (email == null) throw new SecurityException("Not authenticated.");
        return userService.findUserByUsername(email).getId();
    }
}

package com.brahmavanam.japa.controller;

import com.brahmavanam.calendar.service.UserService;
import com.brahmavanam.japa.dto.JapaTargetDTO;
import com.brahmavanam.japa.service.JapaTargetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/japa/target")
public class JapaTargetController {

    @Autowired
    private JapaTargetService japaTargetService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<JapaTargetDTO> setTarget(@RequestBody JapaTargetDTO dto, HttpSession session) {
        int userId = getAuthenticatedUserId(session);
        return ResponseEntity.ok(japaTargetService.setTarget(userId, dto));
    }

    @GetMapping
    public ResponseEntity<JapaTargetDTO> getCurrentTarget(HttpSession session) {
        int userId = getAuthenticatedUserId(session);
        JapaTargetDTO target = japaTargetService.getCurrentTarget(userId);
        return target != null ? ResponseEntity.ok(target) : ResponseEntity.noContent().build();
    }

    private int getAuthenticatedUserId(HttpSession session) {
        String email = (String) session.getAttribute("user");
        if (email == null) throw new IllegalStateException("Not authenticated.");
        return userService.findUserByUsername(email).getId();
    }
}

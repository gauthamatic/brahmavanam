package com.brahmavanam.japa.controller;

import com.brahmavanam.calendar.dto.UserDTO;
import com.brahmavanam.calendar.service.UserService;
import com.brahmavanam.japa.dto.JapaLogDTO;
import com.brahmavanam.japa.service.JapaLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/japa/log")
public class JapaLogController {

    @Autowired
    private JapaLogService japaLogService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<JapaLogDTO> save(@RequestBody JapaLogDTO dto, HttpSession session) {
        int userId = getAuthenticatedUserId(session);
        return ResponseEntity.ok(japaLogService.save(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<JapaLogDTO>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpSession session) {
        int userId = getAuthenticatedUserId(session);
        return ResponseEntity.ok(japaLogService.getByDate(userId, date));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JapaLogDTO> update(@PathVariable Long id,
                                              @RequestBody JapaLogDTO dto,
                                              HttpSession session) {
        int userId = getAuthenticatedUserId(session);
        return ResponseEntity.ok(japaLogService.update(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpSession session) {
        int userId = getAuthenticatedUserId(session);
        japaLogService.delete(userId, id);
        return ResponseEntity.ok().build();
    }

    private int getAuthenticatedUserId(HttpSession session) {
        String email = (String) session.getAttribute("user");
        if (email == null) throw new IllegalStateException("Not authenticated.");
        return userService.findUserByUsername(email).getId();
    }
}

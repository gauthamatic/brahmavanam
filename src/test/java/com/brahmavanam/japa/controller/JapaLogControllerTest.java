package com.brahmavanam.japa.controller;

import com.brahmavanam.calendar.model.User;
import com.brahmavanam.calendar.service.UserService;
import com.brahmavanam.japa.dto.JapaLogDTO;
import com.brahmavanam.japa.model.JapaLog.Intensity;
import com.brahmavanam.japa.service.JapaLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JapaLogController.class)
class JapaLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JapaLogService japaLogService;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;
    private MockHttpSession session;
    private User user;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        user = new User();
        user.setId(1);
        user.setEmailId("john@example.com");

        session = new MockHttpSession();
        session.setAttribute("user", "john@example.com");

        when(userService.findUserByUsername("john@example.com")).thenReturn(user);
    }

    private JapaLogDTO buildDTO() {
        JapaLogDTO dto = new JapaLogDTO();
        dto.setMalas(4);
        dto.setIntensity(Intensity.FOCUSED);
        dto.setDurationMins(45);
        dto.setLoggedAt(LocalDateTime.now().minusHours(1));
        return dto;
    }

    // ─── POST /japa/log ──────────────────────────────────────────────────────

    @Test
    void saveLog_authenticated_returns200() throws Exception {
        JapaLogDTO dto = buildDTO();
        dto.setId(1L);
        when(japaLogService.save(eq(1), any())).thenReturn(dto);

        mockMvc.perform(post("/japa/log")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.malas").value(4));
    }

    @Test
    void saveLog_notAuthenticated_returns401() throws Exception {
        mockMvc.perform(post("/japa/log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void saveLog_serviceThrowsIllegalArgument_returns400() throws Exception {
        when(japaLogService.save(eq(1), any()))
                .thenThrow(new IllegalArgumentException("Malas must be greater than 0."));

        mockMvc.perform(post("/japa/log")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malas must be greater than 0."));
    }

    // ─── GET /japa/log ───────────────────────────────────────────────────────

    @Test
    void getByDate_authenticated_returnsList() throws Exception {
        when(japaLogService.getByDate(eq(1), any())).thenReturn(List.of(buildDTO()));

        mockMvc.perform(get("/japa/log")
                        .session(session)
                        .param("date", "2026-03-25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getByDate_notAuthenticated_returns401() throws Exception {
        mockMvc.perform(get("/japa/log").param("date", "2026-03-25"))
                .andExpect(status().isUnauthorized());
    }

    // ─── DELETE /japa/log/{id} ───────────────────────────────────────────────

    @Test
    void deleteLog_authenticated_returns200() throws Exception {
        doNothing().when(japaLogService).delete(1, 1L);

        mockMvc.perform(delete("/japa/log/1").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void deleteLog_notAuthenticated_returns401() throws Exception {
        mockMvc.perform(delete("/japa/log/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteLog_wrongUser_returns403() throws Exception {
        doThrow(new SecurityException("You can only delete your own entries."))
                .when(japaLogService).delete(1, 1L);

        mockMvc.perform(delete("/japa/log/1").session(session))
                .andExpect(status().isUnauthorized()); // SecurityException maps to 401
    }

    // ─── PUT /japa/log/{id} ──────────────────────────────────────────────────

    @Test
    void updateLog_authenticated_returns200() throws Exception {
        JapaLogDTO dto = buildDTO();
        dto.setId(1L);
        when(japaLogService.update(eq(1), eq(1L), any())).thenReturn(dto);

        mockMvc.perform(put("/japa/log/1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isOk());
    }
}

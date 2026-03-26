package com.brahmavanam.japa.controller;

import com.brahmavanam.calendar.model.User;
import com.brahmavanam.calendar.service.UserService;
import com.brahmavanam.japa.dto.JapaTargetDTO;
import com.brahmavanam.japa.service.JapaTargetService;
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

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JapaTargetController.class)
class JapaTargetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JapaTargetService japaTargetService;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        User user = new User();
        user.setId(1);
        user.setEmailId("john@example.com");

        session = new MockHttpSession();
        session.setAttribute("user", "john@example.com");

        when(userService.findUserByUsername("john@example.com")).thenReturn(user);
    }

    @Test
    void setTarget_authenticated_returns200() throws Exception {
        JapaTargetDTO dto = new JapaTargetDTO();
        dto.setTargetMalas(8);
        dto.setEffectiveFrom(LocalDate.now());
        when(japaTargetService.setTarget(eq(1), any())).thenReturn(dto);

        mockMvc.perform(post("/japa/target")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetMalas").value(8));
    }

    @Test
    void setTarget_notAuthenticated_returns401() throws Exception {
        JapaTargetDTO dto = new JapaTargetDTO();
        dto.setTargetMalas(8);

        mockMvc.perform(post("/japa/target")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void setTarget_invalidValue_returns400() throws Exception {
        JapaTargetDTO dto = new JapaTargetDTO();
        dto.setTargetMalas(-1);
        when(japaTargetService.setTarget(eq(1), any()))
                .thenThrow(new IllegalArgumentException("Target malas must be greater than 0."));

        mockMvc.perform(post("/japa/target")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Target malas must be greater than 0."));
    }

    @Test
    void getCurrentTarget_noTarget_returns204() throws Exception {
        when(japaTargetService.getCurrentTarget(1)).thenReturn(null);

        mockMvc.perform(get("/japa/target").session(session))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCurrentTarget_hasTarget_returns200() throws Exception {
        JapaTargetDTO dto = new JapaTargetDTO();
        dto.setTargetMalas(8);
        when(japaTargetService.getCurrentTarget(1)).thenReturn(dto);

        mockMvc.perform(get("/japa/target").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetMalas").value(8));
    }
}

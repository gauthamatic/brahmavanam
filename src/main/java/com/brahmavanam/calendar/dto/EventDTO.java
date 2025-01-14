package com.brahmavanam.calendar.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EventDTO {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private RRuleDTO rrule;
    private UserDTO user;
    private String color;
    private String textColor;
}
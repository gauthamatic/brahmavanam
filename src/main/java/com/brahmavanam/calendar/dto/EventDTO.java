package com.brahmavanam.calendar.dto;

import lombok.Data;

@Data
public class EventDTO {
    private String title;
    private String start;
    private String end;
    private RRuleDTO rrule;
    private UserDTO user;
    private String color;
    private String textColor;
}
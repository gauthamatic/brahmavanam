package com.brahmavanam.calendar.dto;

import lombok.Data;

@Data
public class RRuleDTO {
    private String freq;
    private int interval;
    private String[] byweekday;
    private String dtstart;
}
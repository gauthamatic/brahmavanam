package com.brahmavanam.model;

import lombok.Data;

@Data
public class RRule {

    private String freq;
    private int interval;
    private String[] byweekday;
    private String dtstart;

}
package com.brahmavanam.model;

import lombok.Data;


@Data
public class Event {
    private String title;
    private RRule rrule;
    private String color;
    private String textColor;

}
package com.brahmavanam.calendar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;

import java.io.Serializable;


@Data
@Entity
public class Event implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String dtstart;
    private String dtend;

    @ManyToOne
    @JoinColumn(name = "rrule_id")
    private RRule rrule;
    private String color;
    private String textColor;

}
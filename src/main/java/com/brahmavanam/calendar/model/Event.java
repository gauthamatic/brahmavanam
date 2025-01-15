package com.brahmavanam.calendar.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;


@Data
@Entity
@Table(name = "event")
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @ManyToOne
    @JoinColumn(name = "rrule_id", referencedColumnName = "id", nullable = true) // Reference to RRule table
    private RRule rrule;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true) // Reference to User table
    private User user;

    @Column
    private String color;

    @Column(name = "text_color") // Explicit mapping for `text_color`
    private String textColor;
}
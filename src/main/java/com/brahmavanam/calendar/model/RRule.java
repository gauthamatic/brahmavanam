package com.brahmavanam.calendar.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class RRule implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String freq;
    @Column(name = "interval_value")
    private int interval;
//    @ElementCollection
//    @CollectionTable(name = "rrule_byweekday", joinColumns = @JoinColumn(name = "rrule_id"))
//    @Column(name = "byweekday")
    private String byweekday;
    private String dtstart;
}
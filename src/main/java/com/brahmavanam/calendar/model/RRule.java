package com.brahmavanam.calendar.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class RRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String freq;
    private int interval;
    @ElementCollection
    @CollectionTable(name = "rrule_byweekday", joinColumns = @JoinColumn(name = "rrule_id"))
    @Column(name = "byweekday")
    private List<String> byweekday;
    private String dtstart;

}
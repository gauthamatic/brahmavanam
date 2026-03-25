package com.brahmavanam.japa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "japa_log")
public class JapaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(nullable = false)
    private int malas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Intensity intensity;

    @Column(name = "duration_mins")
    private Integer durationMins;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum Intensity {
        DISTRACTED, MODERATE, FOCUSED, DEEP
    }
}

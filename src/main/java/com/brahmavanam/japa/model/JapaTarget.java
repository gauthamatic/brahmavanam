package com.brahmavanam.japa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "japa_target")
public class JapaTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "target_malas", nullable = false)
    private int targetMalas;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;
}

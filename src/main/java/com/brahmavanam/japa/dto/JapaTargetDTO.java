package com.brahmavanam.japa.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class JapaTargetDTO {
    private Long id;
    private int targetMalas;
    private LocalDate effectiveFrom;
}

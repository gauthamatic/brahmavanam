package com.brahmavanam.japa.dto;

import com.brahmavanam.japa.model.JapaLog.Intensity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JapaLogDTO {
    private Long id;
    private int malas;
    private Intensity intensity;
    private Integer durationMins;
    private LocalDateTime loggedAt;
    private String notes;
}

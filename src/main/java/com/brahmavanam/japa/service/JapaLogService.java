package com.brahmavanam.japa.service;

import com.brahmavanam.japa.dto.JapaLogDTO;
import com.brahmavanam.japa.model.JapaLog;
import com.brahmavanam.japa.repository.JapaLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JapaLogService {

    @Autowired
    private JapaLogRepository japaLogRepository;

    public JapaLogDTO save(int userId, JapaLogDTO dto) {
        if (dto.getMalas() <= 0) throw new IllegalArgumentException("Malas must be greater than 0.");
        if (dto.getDurationMins() == null || dto.getDurationMins() <= 0) throw new IllegalArgumentException("Duration must be greater than 0.");
        if (dto.getIntensity() == null) throw new IllegalArgumentException("Intensity is required.");

        JapaLog log = toEntity(dto);
        log.setUserId(userId);
        if (log.getLoggedAt() == null) {
            log.setLoggedAt(LocalDateTime.now());
        }
        if (log.getLoggedAt().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Log time cannot be in the future.");
        }
        // Prevent duplicate entry at the exact same timestamp
        List<JapaLog> existing = japaLogRepository
                .findByUserIdAndLoggedAtBetweenOrderByLoggedAtDesc(
                        userId, log.getLoggedAt(), log.getLoggedAt());
        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("An entry already exists for this exact time.");
        }
        return toDTO(japaLogRepository.save(log));
    }

    public List<JapaLogDTO> getByDate(int userId, LocalDate date) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();
        return japaLogRepository
                .findByUserIdAndLoggedAtBetweenOrderByLoggedAtDesc(userId, from, to)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public JapaLogDTO update(int userId, Long id, JapaLogDTO dto) {
        JapaLog existing = japaLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found."));
        if (existing.getUserId() != userId) {
            throw new IllegalArgumentException("You can only edit your own entries.");
        }
        existing.setMalas(dto.getMalas());
        existing.setIntensity(dto.getIntensity());
        existing.setDurationMins(dto.getDurationMins());
        existing.setLoggedAt(dto.getLoggedAt());
        existing.setNotes(dto.getNotes());
        return toDTO(japaLogRepository.save(existing));
    }

    public void delete(int userId, Long id) {
        JapaLog existing = japaLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found."));
        if (existing.getUserId() != userId) {
            throw new SecurityException("You can only delete your own entries.");
        }
        japaLogRepository.delete(existing);
    }

    private JapaLog toEntity(JapaLogDTO dto) {
        JapaLog log = new JapaLog();
        log.setMalas(dto.getMalas());
        log.setIntensity(dto.getIntensity());
        log.setDurationMins(dto.getDurationMins());
        log.setLoggedAt(dto.getLoggedAt());
        log.setNotes(dto.getNotes());
        return log;
    }

    public JapaLogDTO toDTO(JapaLog log) {
        JapaLogDTO dto = new JapaLogDTO();
        dto.setId(log.getId());
        dto.setMalas(log.getMalas());
        dto.setIntensity(log.getIntensity());
        dto.setDurationMins(log.getDurationMins());
        dto.setLoggedAt(log.getLoggedAt());
        dto.setNotes(log.getNotes());
        return dto;
    }
}

package com.brahmavanam.japa.service;

import com.brahmavanam.japa.dto.JapaTargetDTO;
import com.brahmavanam.japa.model.JapaTarget;
import com.brahmavanam.japa.repository.JapaTargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class JapaTargetService {

    @Autowired
    private JapaTargetRepository japaTargetRepository;

    public JapaTargetDTO setTarget(int userId, JapaTargetDTO dto) {
        if (dto.getTargetMalas() <= 0) throw new IllegalArgumentException("Target malas must be greater than 0.");
        LocalDate effectiveFrom = dto.getEffectiveFrom() != null ? dto.getEffectiveFrom() : LocalDate.now();

        // If a target already exists for the same effectiveFrom date, update it instead of inserting
        Optional<JapaTarget> existing = japaTargetRepository
                .findTopByUserIdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(userId, effectiveFrom);

        JapaTarget target = existing.filter(t -> t.getEffectiveFrom().equals(effectiveFrom))
                .orElse(new JapaTarget());

        target.setUserId(userId);
        target.setTargetMalas(dto.getTargetMalas());
        target.setEffectiveFrom(effectiveFrom);
        return toDTO(japaTargetRepository.save(target));
    }

    public JapaTargetDTO getCurrentTarget(int userId) {
        Optional<JapaTarget> target = japaTargetRepository
                .findTopByUserIdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(userId, LocalDate.now());
        return target.map(this::toDTO).orElse(null);
    }

    private JapaTargetDTO toDTO(JapaTarget target) {
        JapaTargetDTO dto = new JapaTargetDTO();
        dto.setId(target.getId());
        dto.setTargetMalas(target.getTargetMalas());
        dto.setEffectiveFrom(target.getEffectiveFrom());
        return dto;
    }
}

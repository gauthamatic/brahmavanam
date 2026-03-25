package com.brahmavanam.japa.repository;

import com.brahmavanam.japa.model.JapaTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface JapaTargetRepository extends JpaRepository<JapaTarget, Long> {

    // Get the most recent target effective on or before a given date
    Optional<JapaTarget> findTopByUserIdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(int userId, LocalDate date);
}

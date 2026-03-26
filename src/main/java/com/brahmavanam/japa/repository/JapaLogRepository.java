package com.brahmavanam.japa.repository;

import com.brahmavanam.japa.model.JapaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JapaLogRepository extends JpaRepository<JapaLog, Long> {

    List<JapaLog> findByUserIdAndLoggedAtBetweenOrderByLoggedAtDesc(int userId, LocalDateTime from, LocalDateTime to);

    List<JapaLog> findByUserIdOrderByLoggedAtDesc(int userId);

    @Query("SELECT DISTINCT CAST(j.loggedAt AS LocalDate) FROM JapaLog j WHERE j.userId = :userId ORDER BY 1")
    List<java.time.LocalDate> findDistinctLogDatesByUserId(int userId);
}

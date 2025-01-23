package com.brahmavanam.calendar.repository;

import com.brahmavanam.calendar.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUserIdAndStartDateAfter(int id, String ninetyDaysAgo);

    List<Event> findByUserId(int id);
}

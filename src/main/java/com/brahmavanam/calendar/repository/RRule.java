package com.brahmavanam.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RRule extends JpaRepository<RRule, Long> {

}

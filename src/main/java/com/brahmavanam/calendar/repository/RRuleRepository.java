package com.brahmavanam.calendar.repository;

import com.brahmavanam.calendar.model.RRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RRuleRepository extends JpaRepository<RRule, Long> {

}

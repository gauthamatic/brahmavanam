package com.brahmavanam.calendar.repository;

import com.brahmavanam.calendar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Query method to find a user by email
    User findByEmailId(String emailId);
}

package com.brahmavanam.calendar.service;


import com.brahmavanam.calendar.model.User;
import com.brahmavanam.calendar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean validateUser(String email, String password) {
        User user = userRepository.findByEmailId(email);
        if (user != null) {
            // Compare passwords (hashing recommended)
            return user.getPassword().equals(password);
        }
        return false;
    }
}

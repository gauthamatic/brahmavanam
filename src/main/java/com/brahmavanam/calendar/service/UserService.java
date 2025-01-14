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
        User user = null;
        try {
            user = userRepository.findByEmailId(email).get();
        } catch (Exception e) {
            return false;
        }
        // Compare passwords (hashing recommended)
        return user.getPassword().equals(password);
    }

    public boolean checkIfUserExists(String email) {
        return userRepository.findByEmailId(email).isPresent();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}

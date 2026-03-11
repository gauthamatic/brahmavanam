package com.brahmavanam.calendar.service;

import com.brahmavanam.calendar.model.User;
import com.brahmavanam.calendar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmailId("john@example.com");
        user.setPassword("password123");
    }

    @Test
    void validateUser_correctCredentials_returnsTrue() {
        when(userRepository.findByEmailId("john@example.com")).thenReturn(Optional.of(user));

        assertTrue(userService.validateUser("john@example.com", "password123"));
    }

    @Test
    void validateUser_wrongPassword_returnsFalse() {
        when(userRepository.findByEmailId("john@example.com")).thenReturn(Optional.of(user));

        assertFalse(userService.validateUser("john@example.com", "wrongpassword"));
    }

    @Test
    void validateUser_userNotFound_returnsFalse() {
        when(userRepository.findByEmailId("unknown@example.com")).thenReturn(Optional.empty());

        assertFalse(userService.validateUser("unknown@example.com", "password123"));
    }

    @Test
    void checkIfUserExists_existingEmail_returnsTrue() {
        when(userRepository.findByEmailId("john@example.com")).thenReturn(Optional.of(user));

        assertTrue(userService.checkIfUserExists("john@example.com"));
    }

    @Test
    void checkIfUserExists_newEmail_returnsFalse() {
        when(userRepository.findByEmailId("new@example.com")).thenReturn(Optional.empty());

        assertFalse(userService.checkIfUserExists("new@example.com"));
    }

    @Test
    void saveUser_callsRepositorySave() {
        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findUserByUsername_returnsUser() {
        when(userRepository.findByEmailId("john@example.com")).thenReturn(Optional.of(user));

        User found = userService.findUserByUsername("john@example.com");

        assertEquals("john@example.com", found.getEmailId());
    }

    @Test
    void findUserById_returnsUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User found = userService.findUserById(1);

        assertEquals(1, found.getId());
    }
}

package com.brahmavanam.calendar.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String password;
}
package com.coworking.backend.dto;

import lombok.Data;

@Data
public class ProfileUpdateDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String profileImagePath;
}

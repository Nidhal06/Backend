package com.coworking.system.dto;

import lombok.Data;
import java.util.Set;

@Data
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String profileImagePath;
    private Set<String> roles;

    public AuthResponse(String token, Long id, String username, String email, 
                       String phone, String profileImagePath, Set<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.profileImagePath = profileImagePath;
        this.roles = roles;
    }
}
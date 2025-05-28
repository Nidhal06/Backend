package com.coworking.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private boolean enabled;
    private String profileImagePath;
    private String type;
    
	public UserDTO(String username, String email, String profileImagePath, String type) {
		super();
		this.username = username;
		this.email = email;
		this.profileImagePath = profileImagePath;
		this.type = type;
	}  
}
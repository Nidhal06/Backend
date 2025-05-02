package com.coworking.system.dto;

import lombok.Data;
import java.util.Set;
import jakarta.validation.constraints.*;

@Data
public class UserDto {
    private Long id;
    
    @NotBlank
    @Size(max = 20)
    private String username;
    
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    
    @Size(max = 120)
    private String password;
    
    @Size(max = 100)
    private String firstName;
    
    @Size(max = 100)
    private String lastName;
    
    private String profileImagePath;
    
    @Size(max = 8)  
    private String phone;
    
    private Set<String> roles;
    
    private String type;
    
    private Boolean enabled;  
    
}

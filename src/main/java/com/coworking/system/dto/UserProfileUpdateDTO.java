package com.coworking.system.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UserProfileUpdateDTO {
    @Size(max = 100)
    private String firstName;
    
    @Size(max = 100)
    private String lastName;
    
    @Size(max = 255)
    private String profileImagePath;
    
    @Size(max = 8)  
    private String phone;
}

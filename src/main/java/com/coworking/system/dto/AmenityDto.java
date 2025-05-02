package com.coworking.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AmenityDto {
    @NotBlank
    private String name;
    
    private String description;
    
    public AmenityDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
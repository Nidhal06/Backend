package com.coworking.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OpenSpaceDto {
    private Long id;
    
    @NotBlank
    private String name;
    
    private String description;
    
    @NotNull
    @Positive
    private Integer capacity;
    
    private Boolean isActive;
}

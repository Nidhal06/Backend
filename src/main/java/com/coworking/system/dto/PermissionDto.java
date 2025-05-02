package com.coworking.system.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class PermissionDto {
    @NotBlank
    private String name;
    
    private String description;
}
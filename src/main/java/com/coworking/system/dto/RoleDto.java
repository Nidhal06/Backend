package com.coworking.system.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

@Data
public class RoleDto {
    @NotBlank
    private String name;
    
    private Set<Long> permissionIds;
}
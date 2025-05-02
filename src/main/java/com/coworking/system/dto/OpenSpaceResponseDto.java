package com.coworking.system.dto;

import lombok.Data;

@Data
public class OpenSpaceResponseDto {
    private Long id;
    private String name;    
    private String description;
    private Integer capacity;
    private String location; 
    private Boolean isActive;

}

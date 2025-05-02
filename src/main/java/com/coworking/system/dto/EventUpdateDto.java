package com.coworking.system.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EventUpdateDto {
    @Size(max = 100)
    private String title;
    
    @Size(max = 500)
    private String description;
    
    @Future
    private LocalDateTime startTime;
    
    @Future 
    private LocalDateTime endTime;
    
    private Long privateSpaceId;
    
    @Positive
    private Integer maxParticipants;
    
    @PositiveOrZero
    private Double price;
}

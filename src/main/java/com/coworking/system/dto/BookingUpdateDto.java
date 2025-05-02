package com.coworking.system.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import lombok.Data;

@Data
public class BookingUpdateDto {
    @Future
    private LocalDateTime startTime;
    
    @Future
    private LocalDateTime endTime;
    
    private String specialRequirements;
}

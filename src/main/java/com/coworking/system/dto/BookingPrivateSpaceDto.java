package com.coworking.system.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingPrivateSpaceDto {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Private space ID is required")
    private Long privateSpaceId;
    
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
    
    private String specialRequirements;
    
    // Méthode de validation
    public void validate() {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        if (startTime.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IllegalArgumentException("Booking must be made at least 1 hour in advance");
        }
    }
}
package com.coworking.system.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingDto {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Space ID is required")
    private Long spaceId;
    
    private Boolean isPrivateSpace;
    
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
    
    private String specialRequirements;
    
    private Long subscriptionId;
    
    // Méthode de validation supplémentaire
    public void validate() {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Booking must be in the future");
        }
        if (isPrivateSpace == null) {
            throw new IllegalArgumentException("Space type must be specified");
        }
    }
}
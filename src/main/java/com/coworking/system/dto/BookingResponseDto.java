package com.coworking.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponseDto {
    private Long id;
    private UserDto user;
    private OpenSpaceDto openSpace;
    private PrivateSpaceDto privateSpace;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalPrice;
    private String status;
    private String specialRequirements;
    private SubscriptionResponseDto subscription;
    private LocalDateTime createdAt;
    
    // Méthode utilitaire pour obtenir le type d'espace
    public String getSpaceType() {
        return privateSpace != null ? "PRIVATE" : "OPEN";
    }
    
    // Méthode utilitaire pour obtenir l'ID de l'espace
    public Long getSpaceId() {
        return privateSpace != null ? privateSpace.getId() : openSpace.getId();
    }
}
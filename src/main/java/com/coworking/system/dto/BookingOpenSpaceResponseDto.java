package com.coworking.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingOpenSpaceResponseDto {
    private Long id;
    private UserDto user;
    private OpenSpaceDto openSpace;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalPrice;
    private String status;
    private String specialRequirements;
    private SubscriptionResponseDto subscription;
    private LocalDateTime createdAt;
}
package com.coworking.system.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingPrivateSpaceResponseDto {
    private Long id;
    private UserDto user;
    private PrivateSpaceDto privateSpace;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalPrice;
    private String status;
    private String specialRequirements;
    private LocalDateTime createdAt;
}
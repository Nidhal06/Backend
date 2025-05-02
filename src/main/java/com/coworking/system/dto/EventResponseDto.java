package com.coworking.system.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class EventResponseDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private PrivateSpaceDto privateSpace;
    private Integer maxParticipants;
    private Double price;
    private Boolean isActive;
    private Set<UserDto> participants;
}
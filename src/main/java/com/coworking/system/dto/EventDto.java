package com.coworking.system.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class EventDto {
    @NotBlank
    private String title;
    
    private String description;
    
    @NotNull
    @Future
    private LocalDateTime startTime;
    
    @NotNull
    @Future
    private LocalDateTime endTime;
    
    @NotNull
    private Long privateSpaceId;
    
    @NotNull
    @Positive
    private Integer maxParticipants;
    
    @NotNull
    @PositiveOrZero
    private Double price;
}

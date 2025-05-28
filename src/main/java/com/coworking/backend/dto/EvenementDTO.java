package com.coworking.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EvenementDTO {
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double price;
    private Integer maxParticipants;
    private boolean isActive;
    private List<ParticipantDTO> participants; 
    private Long espaceId;
    private String espaceName;
}

package com.coworking.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IndisponibiliteDTO {
    private Long id;
    private Long espaceId;
    private String espaceName;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String raison;
}
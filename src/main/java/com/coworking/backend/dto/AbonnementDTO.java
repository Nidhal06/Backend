package com.coworking.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AbonnementDTO {
    private Long id;
    private String type;
    private double prix;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long userId;
    private String userEmail;
    private Long espaceOuvertId;
    private String espaceOuvertName;
}
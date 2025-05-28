package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Indisponibilite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Espace espace;
    
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String raison;
}
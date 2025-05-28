package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Abonnement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private AbonnementType type;
    
    private double prix;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "espace_ouvert_id", nullable = false)
    private EspaceOuvert espaceOuvert;
    
    @OneToOne(mappedBy = "abonnement", cascade = CascadeType.ALL)
    private Paiement paiement;
    
    public enum AbonnementType {
        MENSUEL, ANNUEL
    }
}

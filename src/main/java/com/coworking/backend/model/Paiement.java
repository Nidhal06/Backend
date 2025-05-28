package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private PaiementType type;
    
    private double montant;
    private LocalDateTime date;
    
    @Enumerated(EnumType.STRING)
    private PaiementStatut statut;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abonnement_id")
    private Abonnement abonnement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;
    
    @OneToOne(mappedBy = "paiement")
    private Facture facture;
    
    public enum PaiementType {
        RESERVATION, 
        EVENEMENT, 
        ABONNEMENT
    }

    public enum PaiementStatut {
        EN_ATTENTE, VALIDE, ANNULE
    }
}
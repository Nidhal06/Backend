package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    private Paiement paiement;
    
    private String pdfUrl;
    private LocalDateTime dateEnvoi;
    private String emailDestinataire;
}
package com.coworking.backend.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class EspacePrive extends Espace {
    private double prixParJour;
    
    @ElementCollection
    private List<String> amenities;
    
    @OneToMany(mappedBy = "espace")
    private List<Reservation> reservations;
    
    @OneToMany(mappedBy = "espace")
    private List<Evenement> evenements;
    
    @OneToMany(mappedBy = "espace")
    private List<Avis> avis;
}
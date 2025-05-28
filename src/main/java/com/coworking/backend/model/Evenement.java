package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Evenement {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    private String titre;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double price;
    private Integer maxParticipants;
    private boolean isActive;
    
    @ManyToMany
    @JoinTable(
        name = "evenement_participants",
        joinColumns = @JoinColumn(name = "evenement_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<User> participants = new HashSet<>();
    
    @ManyToOne
    private EspacePrive espace;
    
    @OneToMany(mappedBy = "evenement")
    private Set<Paiement> paiements = new HashSet<>();
   
}
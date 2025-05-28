package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Espace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private int capacity;
    private String photoPrincipal;
    
    @ElementCollection
    private List<String> gallery;
    
    private boolean isActive;
    
    @Enumerated(EnumType.STRING)
    private EspaceType type;
    
    @OneToMany(mappedBy = "espace")
    private List<Indisponibilite> indisponibilites;
    
    public enum EspaceType {
        OUVERT, PRIVE
    }
}

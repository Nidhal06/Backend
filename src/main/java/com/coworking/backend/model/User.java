package com.coworking.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private boolean enabled;
    private String profileImagePath;
    
    @Enumerated(EnumType.STRING)
    private UserType type;
    
    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations;
    
    @OneToMany(mappedBy = "user")
    private List<Abonnement> abonnements;
    
    @OneToMany(mappedBy = "user")
    private List<Avis> avis;
    
    @ManyToMany(mappedBy = "participants")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Evenement> evenements = new HashSet<>();
    
    public enum UserType {
        ADMIN, COWORKER, RECEPTIONISTE
    }
}
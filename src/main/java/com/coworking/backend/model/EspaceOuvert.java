package com.coworking.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class EspaceOuvert extends Espace {
    @OneToMany(mappedBy = "espaceOuvert")
    private List<Abonnement> abonnements;
}
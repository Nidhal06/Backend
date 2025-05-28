package com.coworking.backend.repository;

import com.coworking.backend.model.Evenement;
import com.coworking.backend.model.Paiement;
import com.coworking.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    boolean existsByUserAndEvenement(User user, Evenement evenement);
}
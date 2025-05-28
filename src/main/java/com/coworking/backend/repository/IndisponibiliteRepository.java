package com.coworking.backend.repository;

import com.coworking.backend.model.Indisponibilite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IndisponibiliteRepository extends JpaRepository<Indisponibilite, Long> {
    List<Indisponibilite> findByEspaceIdAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
            Long espaceId, LocalDateTime dateFin, LocalDateTime dateDebut);
}
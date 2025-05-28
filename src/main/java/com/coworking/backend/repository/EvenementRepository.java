package com.coworking.backend.repository;

import com.coworking.backend.model.Evenement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    @EntityGraph(attributePaths = {"participants"})
    @Query("SELECT e FROM Evenement e WHERE e.id = :id")
    Optional<Evenement> findByIdWithParticipants(@Param("id") Long id);
}
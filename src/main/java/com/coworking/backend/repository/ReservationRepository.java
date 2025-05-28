package com.coworking.backend.repository;

import com.coworking.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByEspaceIdAndDateDebutBetweenOrDateFinBetween(
            Long espaceId, 
            LocalDateTime start1, LocalDateTime end1, 
            LocalDateTime start2, LocalDateTime end2);
    
    List<Reservation> findByEspaceId(Long espaceId);
}
package com.coworking.backend.repository;

import com.coworking.backend.model.Avis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvisRepository extends JpaRepository<Avis, Long> {
    List<Avis> findByEspaceId(Long espaceId);
}
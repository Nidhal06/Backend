package com.coworking.backend.repository;

import com.coworking.backend.model.Espace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EspaceRepository extends JpaRepository<Espace, Long> {
	List<Espace> findByType(String type);
}
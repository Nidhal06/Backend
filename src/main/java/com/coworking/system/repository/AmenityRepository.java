package com.coworking.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.coworking.system.entity.Amenity;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}

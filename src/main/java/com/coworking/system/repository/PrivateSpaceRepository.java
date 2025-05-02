package com.coworking.system.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.coworking.system.entity.PrivateSpace;

public interface PrivateSpaceRepository extends JpaRepository<PrivateSpace, Long> {
    
	@EntityGraph(attributePaths = {"amenities"})
	@Query("SELECT DISTINCT s FROM PrivateSpace s LEFT JOIN FETCH s.amenities")
	List<PrivateSpace> findAllWithAmenities();
}

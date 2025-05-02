package com.coworking.system.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.coworking.system.entity.OpenSpace;

public interface OpenSpaceRepository extends JpaRepository<OpenSpace, Long> {
    List<OpenSpace> findByIsActiveTrue();
}

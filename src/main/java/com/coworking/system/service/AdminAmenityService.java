package com.coworking.system.service;

import java.util.List;
import com.coworking.system.dto.AmenityDto;
import com.coworking.system.entity.Amenity;

public interface AdminAmenityService {
    Amenity create(AmenityDto dto);
    Amenity update(Long id, AmenityDto dto);
    void delete(Long id);
    Amenity getById(Long id);
    List<Amenity> getAll();
}
package com.coworking.system.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.system.dto.AmenityDto;
import com.coworking.system.entity.Amenity;
import com.coworking.system.exception.ResourceNotFoundException;
import com.coworking.system.repository.AmenityRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAmenityServiceImpl implements AdminAmenityService {
    private final AmenityRepository repository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Amenity create(AmenityDto dto) {
        Amenity amenity = modelMapper.map(dto, Amenity.class);
        return repository.save(amenity);
    }

    @Override
    @Transactional
    public Amenity update(Long id, AmenityDto dto) {
        Amenity amenity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", id));
        modelMapper.map(dto, amenity);
        return repository.save(amenity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Amenity amenity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", id));
        repository.delete(amenity);
    }

    @Override
    public Amenity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", id));
    }

    @Override
    public List<Amenity> getAll() {
        return repository.findAll();
    }
}
package com.coworking.system.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.coworking.system.dto.PrivateSpaceDto;
import com.coworking.system.dto.PrivateSpaceResponseDto;
import com.coworking.system.entity.Amenity;
import com.coworking.system.entity.PrivateSpace;
import com.coworking.system.exception.ResourceNotFoundException;
import com.coworking.system.repository.AmenityRepository;
import com.coworking.system.repository.PrivateSpaceRepository;
import com.coworking.system.service.AdminSpaceService;



@RestController
@RequestMapping("/api/admin/spaces")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminSpaceController {
    private final AdminSpaceService service;
    private final PrivateSpaceRepository repository;
    private final AmenityRepository amenityRepository; 
    private final ModelMapper modelMapper;
   
    @GetMapping
    @Operation(summary = "Get all private spaces")
    public ResponseEntity<List<PrivateSpaceResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get private space by ID")
    public ResponseEntity<PrivateSpaceResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping("/{spaceId}/amenities")
    @Operation(summary = "Add amenities to a private space")
    public ResponseEntity<PrivateSpaceResponseDto> addAmenities(
            @PathVariable Long spaceId,
            @RequestBody List<Long> amenityIds) { 
        PrivateSpace space = repository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Private_Space", "id", spaceId));

        Set<Amenity> amenities = amenityIds.stream()
            .map(id -> amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", id)))
            .collect(Collectors.toSet());

        space.getAmenities().addAll(amenities);
        PrivateSpace updated = repository.save(space);
        return ResponseEntity.ok(modelMapper.map(updated, PrivateSpaceResponseDto.class));
    }


    @DeleteMapping("/{spaceId}/amenities")
    @Operation(summary = "Remove amenities from a private space")
    public ResponseEntity<PrivateSpaceResponseDto> removeAmenities(
            @PathVariable Long spaceId,
            @RequestBody Set<Long> amenityIds) {
        PrivateSpace space = repository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Private_Space", "id", spaceId));
        
        space.getAmenities().removeIf(a -> amenityIds.contains(a.getId()));
        PrivateSpace updated = repository.save(space);
        return ResponseEntity.ok(modelMapper.map(updated, PrivateSpaceResponseDto.class));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new private space")
    public ResponseEntity<PrivateSpaceResponseDto> create(
            @RequestPart("private_space") @Valid PrivateSpaceDto dto,
            @RequestPart("photo") MultipartFile photo,
            @RequestPart(value = "gallery", required = false) MultipartFile[] gallery) {
        
        try {
            return ResponseEntity.ok(service.create(dto, photo, gallery));
        } catch (IOException e) {
            throw new RuntimeException("Failed to store files: " + e.getMessage(), e);
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a private space")
    public ResponseEntity<PrivateSpaceResponseDto> update(
        @PathVariable Long id,
        @RequestPart("private_space") @Valid PrivateSpaceDto spaceDto,
        @RequestPart(value = "photo", required = false) MultipartFile photo,
        @RequestPart(value = "gallery", required = false) MultipartFile[] gallery) {
        
        try {
            spaceDto.setId(id);
            return ResponseEntity.ok(service.update(
                id,
                spaceDto,
                photo,
                gallery != null ? gallery : new MultipartFile[0]
            ));
        } catch (IOException e) {
            throw new RuntimeException("Failed to update private space", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a private space")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle private space active status")
    public ResponseEntity<PrivateSpaceResponseDto> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleStatus(id));
    }
}

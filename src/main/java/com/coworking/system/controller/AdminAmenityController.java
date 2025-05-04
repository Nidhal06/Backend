package com.coworking.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.coworking.system.dto.AmenityDto;
import com.coworking.system.entity.Amenity;
import com.coworking.system.service.AdminAmenityService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/amenities")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAmenityController {
    private final AdminAmenityService service;

    @PostMapping
    @Operation(summary = "Create a new amenity")
    public ResponseEntity<Amenity> create(@Valid @RequestBody AmenityDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    @Operation(summary = "Get all amenities")
    public ResponseEntity<List<Amenity>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get amenity by ID")
    public ResponseEntity<Amenity> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an amenity")
    public ResponseEntity<Amenity> update(@PathVariable Long id, @Valid @RequestBody AmenityDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an amenity")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
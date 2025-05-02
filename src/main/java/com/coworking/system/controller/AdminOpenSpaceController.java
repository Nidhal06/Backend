package com.coworking.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.coworking.system.dto.OpenSpaceDto;
import com.coworking.system.dto.OpenSpaceResponseDto;
import com.coworking.system.service.AdminOpenSpaceService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/open-spaces")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminOpenSpaceController {
    private final AdminOpenSpaceService service;

    @PostMapping
    @Operation(summary = "Create a new open space")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OpenSpaceResponseDto> create(@Valid @RequestBody OpenSpaceDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an open space")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OpenSpaceResponseDto> update(@PathVariable Long id, @Valid @RequestBody OpenSpaceDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an open space")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get open space by ID")
    public ResponseEntity<OpenSpaceResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all open spaces")
    public ResponseEntity<List<OpenSpaceResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle open space active status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OpenSpaceResponseDto> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleStatus(id));
    }
}
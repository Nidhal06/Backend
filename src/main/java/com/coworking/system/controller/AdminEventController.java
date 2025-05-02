package com.coworking.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.coworking.system.dto.EventDto;
import com.coworking.system.dto.EventResponseDto;
import com.coworking.system.dto.EventUpdateDto;
import com.coworking.system.service.AdminEventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminEventController {
    private final AdminEventService service;
    
    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID")
    public ResponseEntity<EventResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByIdWithParticipants(id));
    }

    @GetMapping
    @Operation(summary = "Get all events")
    public ResponseEntity<List<EventResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    
    @PreAuthorize("hasRole('COWORKER')")
    @PostMapping("/{eventId}/register/{userId}")
    @Operation(summary = "Register user to an event")
    public ResponseEntity<EventResponseDto> registerUserToEvent(
            @PathVariable Long eventId, 
            @PathVariable Long userId) {
        return ResponseEntity.ok(service.registerUserToEvent(eventId, userId));
    }
    

    @PreAuthorize("hasRole('COWORKER')")
    @PostMapping("/{eventId}/unregister/{userId}")
    @Operation(summary = "Unregister user from an event")
    public ResponseEntity<EventResponseDto> unregisterUserFromEvent(
            @PathVariable Long eventId, 
            @PathVariable Long userId) {
        return ResponseEntity.ok(service.unregisterUserFromEvent(eventId, userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new event")
    public ResponseEntity<EventResponseDto> create(@Valid @RequestBody EventDto dto) {
        try {
            return ResponseEntity.ok(service.create(dto));
        } catch (Exception e) {
            log.error("Error creating event", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating event: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update an event")
    public ResponseEntity<EventResponseDto> update(@PathVariable Long id, @Valid @RequestBody EventUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an event")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle event active status")
    public ResponseEntity<EventResponseDto> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleStatus(id));
    }
}
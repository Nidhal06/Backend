package com.coworking.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.coworking.system.dto.*;
import com.coworking.system.service.UnavailabilityPeriodService;
import java.util.List;

@RestController
@RequestMapping("/api/unavailability-periods")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UnavailabilityPeriodController {
    private final UnavailabilityPeriodService service;

    @PostMapping("/open-space")
    @Operation(summary = "Create unavailability period for open space")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnavailabilityPeriodOpenSpaceResponseDto> createForOpenSpace(
            @Valid @RequestBody UnavailabilityPeriodDto dto) {
        return ResponseEntity.ok(service.createForOpenSpace(dto));
    }

    @PostMapping("/private-space")
    @Operation(summary = "Create unavailability period for private space")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnavailabilityPeriodPrivateSpaceResponseDto> createForPrivateSpace(
            @Valid @RequestBody UnavailabilityPeriodDto dto) {
        return ResponseEntity.ok(service.createForPrivateSpace(dto));
    }

    @GetMapping("/open-space/{openSpaceId}")
    @Operation(summary = "Get open space unavailability periods")
    public ResponseEntity<List<UnavailabilityPeriodOpenSpaceResponseDto>> getOpenSpacePeriods(
            @PathVariable Long openSpaceId) {
        return ResponseEntity.ok(service.getOpenSpaceUnavailabilityPeriods(openSpaceId));
    }

    @GetMapping("/private-space/{privateSpaceId}")
    @Operation(summary = "Get private space unavailability periods")
    public ResponseEntity<List<UnavailabilityPeriodPrivateSpaceResponseDto>> getPrivateSpacePeriods(
            @PathVariable Long privateSpaceId) {
        return ResponseEntity.ok(service.getPrivateSpaceUnavailabilityPeriods(privateSpaceId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete unavailability period")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
package com.coworking.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.coworking.system.dto.SubscriptionDto;
import com.coworking.system.dto.SubscriptionResponseDto;
import com.coworking.system.service.SubscriptionService;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {
    private final SubscriptionService service;

    @PostMapping
    @Operation(summary = "Create a new subscription")
    @PreAuthorize("hasRole('COWORKER')")
    public ResponseEntity<SubscriptionResponseDto> create(@Valid @RequestBody SubscriptionDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user subscriptions")
    public ResponseEntity<List<SubscriptionResponseDto>> getUserSubscriptions(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUserSubscriptions(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscription by ID")
    public ResponseEntity<SubscriptionResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle subscription status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionResponseDto> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleStatus(id));
    }
}
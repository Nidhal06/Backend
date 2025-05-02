package com.coworking.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.coworking.system.dto.BookingResponseDto;
import com.coworking.system.service.AdminBookingService;
import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {
    private final AdminBookingService service;

    @GetMapping
    @Operation(summary = "Get all bookings")
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        return ResponseEntity.ok(service.getAllBookings());
    }

    @GetMapping("/search")
    @Operation(summary = "Search bookings")
    public ResponseEntity<List<BookingResponseDto>> searchBookings(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long spaceId,
            @RequestParam(required = false) Boolean isPrivate) {
        return ResponseEntity.ok(service.searchBookings(userId, spaceId, isPrivate));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking as admin")
    public ResponseEntity<BookingResponseDto> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelBookingAsAdmin(id));
    }
}
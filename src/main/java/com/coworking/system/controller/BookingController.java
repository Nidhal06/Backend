package com.coworking.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.coworking.system.dto.BookingDto;
import com.coworking.system.dto.BookingResponseDto;
import com.coworking.system.service.BookingService;


import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create a new booking")
    @PreAuthorize("hasRole('COWORKER')")
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingDto dto) {
        return ResponseEntity.ok(bookingService.createBooking(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a booking")
    @PreAuthorize("hasRole('COWORKER')")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user bookings")
    public ResponseEntity<List<BookingResponseDto>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/space/{spaceId}")
    @Operation(summary = "Get space bookings")
    public ResponseEntity<List<BookingResponseDto>> getSpaceBookings(
            @PathVariable Long spaceId,
            @RequestParam(required = false, defaultValue = "false") boolean isPrivate) {
        return ResponseEntity.ok(bookingService.getSpaceBookings(spaceId, isPrivate));
    }
}
package com.coworking.backend.controller;

import com.coworking.backend.dto.ReservationDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.User;
import com.coworking.backend.repository.UserRepository;
import com.coworking.backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONISTE')")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('COWORKER')")
    public ResponseEntity<ReservationDTO> createReservation(
            @RequestBody ReservationDTO reservationDTO,
            Principal principal) {
        // Get user email from principal and find user
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Set the user ID from the authenticated user
        reservationDTO.setUserId(user.getId());
        
        return ResponseEntity.ok(reservationService.createReservation(reservationDTO));
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('COWORKER') and #userId == principal.id")
    public ResponseEntity<List<ReservationDTO>> getReservationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getAllReservations().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList()));
    }
    
    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsBySpace(@PathVariable Long spaceId) {
        return ResponseEntity.ok(reservationService.getReservationsBySpace(spaceId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<ReservationDTO> updateReservation(@PathVariable Long id, @RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservationDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER')")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
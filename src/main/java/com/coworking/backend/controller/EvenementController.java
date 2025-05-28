package com.coworking.backend.controller;

import com.coworking.backend.dto.EvenementDTO;
import com.coworking.backend.service.EvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evenements")
@RequiredArgsConstructor
public class EvenementController {

    private final EvenementService evenementService;

    @GetMapping
    public ResponseEntity<List<EvenementDTO>> getAllEvenements() {
        return ResponseEntity.ok(evenementService.getAllEvenements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvenementDTO> getEvenementById(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.getEvenementById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EvenementDTO> createEvenement(@RequestBody EvenementDTO evenementDTO) {
        return ResponseEntity.ok(evenementService.createEvenement(evenementDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EvenementDTO> updateEvenement(@PathVariable Long id, @RequestBody EvenementDTO evenementDTO) {
        return ResponseEntity.ok(evenementService.updateEvenement(id, evenementDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        evenementService.deleteEvenement(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{evenementId}/register/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<EvenementDTO> registerParticipant(
            @PathVariable Long evenementId, @PathVariable Long userId) {
        return ResponseEntity.ok(evenementService.registerParticipant(evenementId, userId));
    }

    @PostMapping("/{evenementId}/cancel/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<EvenementDTO> cancelParticipation(
            @PathVariable Long evenementId, @PathVariable Long userId) {
        return ResponseEntity.ok(evenementService.cancelParticipation(evenementId, userId));
    }
}
package com.coworking.backend.controller;

import com.coworking.backend.dto.AbonnementDTO;
import com.coworking.backend.service.AbonnementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/abonnements")
@RequiredArgsConstructor
public class AbonnementController {

    private final AbonnementService abonnementService;

    @GetMapping
    public ResponseEntity<List<AbonnementDTO>> getAllAbonnements() {
        return ResponseEntity.ok(abonnementService.getAllAbonnements());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<AbonnementDTO> getAbonnementById(@PathVariable Long id) {
        return ResponseEntity.ok(abonnementService.getAbonnementById(id));
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<List<AbonnementDTO>> getAbonnementsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(abonnementService.getAbonnementsByUser(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('COWORKER')")
    public ResponseEntity<AbonnementDTO> createAbonnement(@RequestBody AbonnementDTO abonnementDTO) {
        return ResponseEntity.ok(abonnementService.createAbonnement(abonnementDTO));
    }
    
    @PostMapping("/for-all-coworkers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AbonnementDTO>> createAbonnementsForAllCoworkers(@RequestBody AbonnementDTO abonnementDTO) {
        return ResponseEntity.ok(abonnementService.createAbonnementsForAllCoworkers(abonnementDTO));
    }

    @GetMapping("/check-valid/{userId}/{espaceOuvertId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<Boolean> checkValidAbonnement(
            @PathVariable Long userId, 
            @PathVariable Long espaceOuvertId) {
        return ResponseEntity.ok(abonnementService.hasValidAbonnement(userId, espaceOuvertId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<AbonnementDTO> updateAbonnement(@PathVariable Long id, @RequestBody AbonnementDTO abonnementDTO) {
        return ResponseEntity.ok(abonnementService.updateAbonnement(id, abonnementDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER')")
    public ResponseEntity<Void> deleteAbonnement(@PathVariable Long id) {
        abonnementService.deleteAbonnement(id);
        return ResponseEntity.noContent().build();
    }
}
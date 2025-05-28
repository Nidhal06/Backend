package com.coworking.backend.controller;

import com.coworking.backend.dto.AvisDTO;
import com.coworking.backend.service.AvisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avis")
@RequiredArgsConstructor
public class AvisController {

    private final AvisService avisService;

    @GetMapping
    public ResponseEntity<List<AvisDTO>> getAllAvis() {
        return ResponseEntity.ok(avisService.getAllAvis());
    }

    @GetMapping("/espace/{espaceId}")
    public ResponseEntity<List<AvisDTO>> getAvisByEspaceId(@PathVariable Long espaceId) {
        return ResponseEntity.ok(avisService.getAvisByEspaceId(espaceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvisDTO> getAvisById(@PathVariable Long id) {
        return ResponseEntity.ok(avisService.getAvisById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('COWORKER')")
    public ResponseEntity<AvisDTO> createAvis(@RequestBody AvisDTO avisDTO) {
        return ResponseEntity.ok(avisService.createAvis(avisDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER')")
    public ResponseEntity<Void> deleteAvis(@PathVariable Long id) {
        avisService.deleteAvis(id);
        return ResponseEntity.noContent().build();
    }
}
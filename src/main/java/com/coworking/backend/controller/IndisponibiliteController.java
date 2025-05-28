package com.coworking.backend.controller;

import com.coworking.backend.dto.IndisponibiliteDTO;
import com.coworking.backend.service.IndisponibiliteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/indisponibilites")
@RequiredArgsConstructor
public class IndisponibiliteController {

    private final IndisponibiliteService indisponibiliteService;

    @GetMapping
    public ResponseEntity<List<IndisponibiliteDTO>> getAllIndisponibilites() {
        return ResponseEntity.ok(indisponibiliteService.getAllIndisponibilites());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COWORKER', 'RECEPTIONISTE')")
    public ResponseEntity<IndisponibiliteDTO> getIndisponibiliteById(@PathVariable Long id) {
        return ResponseEntity.ok(indisponibiliteService.getIndisponibiliteById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IndisponibiliteDTO> createIndisponibilite(@RequestBody IndisponibiliteDTO indisponibiliteDTO) {
        return ResponseEntity.ok(indisponibiliteService.createIndisponibilite(indisponibiliteDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IndisponibiliteDTO> updateIndisponibilite(@PathVariable Long id, @RequestBody IndisponibiliteDTO indisponibiliteDTO) {
        return ResponseEntity.ok(indisponibiliteService.updateIndisponibilite(id, indisponibiliteDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteIndisponibilite(@PathVariable Long id) {
        indisponibiliteService.deleteIndisponibilite(id);
        return ResponseEntity.noContent().build();
    }
}
package com.coworking.backend.controller;

import com.coworking.backend.dto.FactureDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.Facture;
import com.coworking.backend.repository.FactureRepository;
import com.coworking.backend.service.FactureService;
import com.coworking.backend.util.PdfGeneratorService;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;
    private final FactureRepository factureRepository;
    private final PdfGeneratorService pdfGeneratorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONISTE')")
    public ResponseEntity<List<FactureDTO>> getAllFactures() {
        return ResponseEntity.ok(factureService.getAllFactures());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONISTE')")
    public ResponseEntity<FactureDTO> getFactureById(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.getFactureById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONISTE')")
    public ResponseEntity<FactureDTO> createFacture(@RequestBody FactureDTO factureDTO) throws DocumentException {
        return ResponseEntity.ok(factureService.createFacture(factureDTO));
    }
    

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONISTE')")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        factureService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONISTE')")
    public ResponseEntity<ByteArrayResource> downloadFacturePdf(
    		@PathVariable Long id,
    	    @RequestHeader(value = "Authorization", required = false) String authHeader 
    	    ) throws DocumentException {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture not found"));
        
        // Générer le PDF à la volée
        ByteArrayOutputStream pdfStream = pdfGeneratorService.generateFacturePdf(facture.getPaiement());
        byte[] pdfBytes = pdfStream.toByteArray();
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture_" + id + ".pdf")
                .body(new ByteArrayResource(pdfBytes));
    }
}
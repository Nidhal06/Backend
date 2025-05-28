package com.coworking.backend.controller;

import com.coworking.backend.dto.EspacePriveDTO;
import com.coworking.backend.service.EspacePriveService;
import com.coworking.backend.util.FileStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/espaces/prives")
@RequiredArgsConstructor
public class EspacePriveController {

    private final EspacePriveService espacePriveService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<EspacePriveDTO>> getAllEspacePrives() {
        return ResponseEntity.ok(espacePriveService.getAllEspacePrives());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspacePriveDTO> getEspacePriveById(@PathVariable Long id) {
        return ResponseEntity.ok(espacePriveService.getEspacePriveById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEspacePrive(
        @RequestPart("data") String espacePriveDTO,
        @RequestPart(value = "photoPrincipal", required = false) MultipartFile photoPrincipal,
        @RequestPart(value = "gallery", required = false) MultipartFile[] gallery) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            EspacePriveDTO dto = mapper.readValue(espacePriveDTO, EspacePriveDTO.class);

            if (photoPrincipal != null) {
                try {
                    String photoPath = fileStorageService.storeFile(photoPrincipal);
                    dto.setPhotoPrincipal(photoPath);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to store main photo: " + e.getMessage());
                }
            }

            if (gallery != null && gallery.length > 0) {
                List<String> galleryPaths = Arrays.stream(gallery)
                    .map(file -> {
                        try {
                            return fileStorageService.storeFile(file);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to store gallery file", e);
                        }
                    })
                    .collect(Collectors.toList());
                dto.setGallery(galleryPaths);
            }

            return ResponseEntity.ok(espacePriveService.createEspacePrive(dto));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid JSON data: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEspacePrive(
        @PathVariable Long id,
        @RequestPart("data") String espacePriveDTO,
        @RequestPart(value = "photoPrincipal", required = false) MultipartFile photoPrincipal,
        @RequestPart(value = "gallery", required = false) MultipartFile[] gallery,
        @RequestPart(value = "imagesToDelete", required = false) String imagesToDelete) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            EspacePriveDTO dto = mapper.readValue(espacePriveDTO, EspacePriveDTO.class);

            // Vérification de cohérence des IDs
            if (dto.getId() != null && !dto.getId().equals(id)) {
                return ResponseEntity.badRequest().body("L'ID dans le chemin et dans le corps de la requête ne correspondent pas");
            }

            // Gestion de la photo principale
            if (photoPrincipal != null && !photoPrincipal.isEmpty()) {
                try {
                    String photoPath = fileStorageService.storeFile(photoPrincipal);
                    dto.setPhotoPrincipal(photoPath);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Échec du stockage de la photo principale : " + e.getMessage());
                }
            }

            // Gestion de la galerie
            if (gallery != null && gallery.length > 0) {
                List<String> galleryPaths = Arrays.stream(gallery)
                    .map(file -> {
                        try {
                            return fileStorageService.storeFile(file);
                        } catch (IOException e) {
                            throw new RuntimeException("Échec du stockage du fichier de la galerie", e);
                        }
                    })
                    .collect(Collectors.toList());
                dto.setGallery(galleryPaths);
            }

            // Gestion des images à supprimer
            if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
                try {
                    List<String> imagesToDeleteList = mapper.readValue(imagesToDelete, new TypeReference<List<String>>() {});
                    fileStorageService.deleteFiles(imagesToDeleteList);
                } catch (JsonProcessingException e) {
                    return ResponseEntity.badRequest().body("Format JSON invalide pour imagesToDelete");
                }
            }

            // Appel du service de mise à jour
            EspacePriveDTO updatedEspace = espacePriveService.updateEspacePrive(id, dto);
            return ResponseEntity.ok(updatedEspace);

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Données JSON invalides : " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEspacePrive(@PathVariable Long id) {
        espacePriveService.deleteEspacePrive(id);
        return ResponseEntity.noContent().build();
    }
}
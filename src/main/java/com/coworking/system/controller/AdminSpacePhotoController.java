package com.coworking.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.coworking.system.service.SpacePhotoService;
import java.io.IOException;

@RestController
@RequestMapping("/api/admin/spaces/{spaceId}/photos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSpacePhotoController {
    private final SpacePhotoService spacePhotoService;

    @PostMapping("/main")
    @Operation(summary = "Upload main photo for a space")
    public ResponseEntity<String> uploadMainPhoto(
            @PathVariable Long spaceId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(spacePhotoService.uploadMainPhoto(spaceId, file));
    }

    @PostMapping("/gallery")
    @Operation(summary = "Add photo to space gallery")
    public ResponseEntity<String> addToGallery(
            @PathVariable Long spaceId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(spacePhotoService.addToGallery(spaceId, file));
    }

    @DeleteMapping("/gallery")
    @Operation(summary = "Remove photo from space gallery")
    public ResponseEntity<Void> removeFromGallery(
            @PathVariable Long spaceId,
            @RequestParam String imagePath) {
        spacePhotoService.removeFromGallery(spaceId, imagePath);
        return ResponseEntity.noContent().build();
    }
}
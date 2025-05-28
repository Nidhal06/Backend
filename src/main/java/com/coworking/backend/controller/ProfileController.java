package com.coworking.backend.controller;

import com.coworking.backend.dto.ProfilDto;
import com.coworking.backend.dto.ProfileUpdateDTO;
import com.coworking.backend.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfilDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        ProfilDto profil = profileService.getCurrentUser(email);
        return ResponseEntity.ok(profil);
    }

  
    @PutMapping
    public ResponseEntity<ProfilDto> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfileUpdateDTO updateDTO) {
        String username = userDetails.getUsername();
        ProfilDto updatedProfil = profileService.updateUserProfile(username, updateDTO);
        return ResponseEntity.ok(updatedProfil);
    }

 
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProfileImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        String username = userDetails.getUsername();
        String imagePath = profileService.updateProfileImage(username, file);
        return ResponseEntity.ok(imagePath);
    }
}

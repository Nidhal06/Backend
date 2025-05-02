package com.coworking.system.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.coworking.system.dto.UserDto;
import com.coworking.system.dto.UserProfileUpdateDTO;
import com.coworking.system.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONIST')")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getCurrentUser(username));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONIST')")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody UserProfileUpdateDTO userProfileUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(userService.updateUserProfile(username, userProfileUpdateDTO));
    }

    @PostMapping("/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'COWORKER', 'RECEPTIONIST')")
    public ResponseEntity<Map<String, String>> updateProfileImage(@RequestParam("file") MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String imagePath = userService.updateProfileImage(username, file);
        return ResponseEntity.ok(Collections.singletonMap("imagePath", imagePath));
    }
}
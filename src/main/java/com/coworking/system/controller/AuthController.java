package com.coworking.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.coworking.system.dto.AuthRequest;
import com.coworking.system.dto.AuthResponse;
import com.coworking.system.dto.SignUpRequest;
import com.coworking.system.entity.User.UserType;
import com.coworking.system.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.authenticateUser(authRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    // Endpoint sécurisé pour créer des comptes système
    @PostMapping("/system/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSystemUser(
            @RequestParam UserType userType,
            @Valid @RequestBody SignUpRequest signUpRequest) {
        authService.createSystemUser(
            signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            signUpRequest.getPassword(),
            signUpRequest.getFirstName(),
            signUpRequest.getLastName(),
            signUpRequest.getPhone(),
            userType
        );
        return ResponseEntity.ok(userType.name() + " created successfully");
    }
}
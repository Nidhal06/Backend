package com.coworking.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.coworking.system.dto.AuthRequest;
import com.coworking.system.dto.AuthResponse;
import com.coworking.system.dto.SignUpRequest;
import com.coworking.system.entity.Role;
import com.coworking.system.entity.User;
import com.coworking.system.entity.User.UserType;
import com.coworking.system.exception.BadRequestException;
import com.coworking.system.repository.*;
import com.coworking.system.security.JwtUtils;
import com.coworking.system.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    public AuthResponse authenticateUser(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(),
                authRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        return new AuthResponse(jwt, 
                              userDetails.getId(), 
                              userDetails.getUsername(),
                              userDetails.getEmail(),
                              userDetails.getPhone(),
                              userDetails.getProfileImagePath(),
                              roles);
    }

    @Transactional
    public void registerUser(SignUpRequest signUpRequest) {
        // Validation du username
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        // Validation de l'email
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        // Validation du téléphone
        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            throw new BadRequestException("Phone number is already in use");
        }

        // Création du nouvel utilisateur
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setPhone(signUpRequest.getPhone());
        user.setType(UserType.valueOf(signUpRequest.getType().name()));
        user.setEnabled(true);
        
        // Optionnel: image de profil
        if (signUpRequest.getProfileImagePath() != null) {
            user.setProfileImagePath(signUpRequest.getProfileImagePath());
        }

     // Chargement EXPLICITE du rôle avec les permissions
        String roleName = "ROLE_" + signUpRequest.getType().name();
        Role userRole = roleRepository.findByNameWithPermissions(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
        
        // Création d'un nouveau HashSet pour éviter les problèmes de référence
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        
        userRepository.save(user);
    }

    // Méthode pour créer les comptes système (admin, réceptionniste)
    @Transactional
    public void createSystemUser(String username, String email, String password, 
                               String firstName, String lastName, String phone, 
                               UserType userType) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username is already taken");
        }

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already in use");
        }

        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new BadRequestException("Phone number is already in use");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setType(userType);
        user.setEnabled(true);

        Set<Role> roles = new HashSet<>();
        String roleName = "ROLE_" + userType.name();
        Role userRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);
    }
}
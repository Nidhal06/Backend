package com.coworking.system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coworking.system.dto.UserDto;
import com.coworking.system.dto.UserResponseDto;
import com.coworking.system.entity.User;
import com.coworking.system.exception.BadRequestException;
import com.coworking.system.exception.ResourceNotFoundException;
import com.coworking.system.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j 
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto create(UserDto dto) {
    	if (dto.getPhone() != null) {
            // Validation de la longueur
            if (dto.getPhone().length() != 8) {
                throw new BadRequestException("Le numéro de téléphone doit contenir 8 chiffres");
            }
            
            // Validation de l'unicité
            if (userRepository.existsByPhone(dto.getPhone())) {
                throw new BadRequestException("Ce numéro de téléphone est déjà utilisé");
            }
        }
        
        User user = modelMapper.map(dto, User.class);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User saved = userRepository.save(user);
        return modelMapper.map(saved, UserResponseDto.class);
    }

    @Transactional
    public UserResponseDto update(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        try {
            // Log pour débogage
            log.info("Updating user ID: {}, Data: {}", id, dto.toString());
            
            // Vérification et mise à jour du username
            if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
                String newUsername = dto.getUsername().trim();
                if (!newUsername.equals(user.getUsername()) && 
                    userRepository.existsByUsername(newUsername)) {
                    throw new BadRequestException("Username is already in use");
                }
                user.setUsername(newUsername);
            }
            
            // Vérification et mise à jour de l'email
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                String newEmail = dto.getEmail().trim();
                if (!newEmail.equals(user.getEmail()) && 
                    userRepository.existsByEmail(newEmail)) {
                    throw new BadRequestException("Email is already in use");
                }
                user.setEmail(newEmail);
            }
            
            // Mise à jour des autres champs
            user.setFirstName(dto.getFirstName() != null ? dto.getFirstName().trim() : null);
            user.setLastName(dto.getLastName() != null ? dto.getLastName().trim() : null);
            user.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
            
            // Gestion du type d'utilisateur
            if (dto.getType() != null && !dto.getType().isBlank()) {
                try {
                    user.setType(User.UserType.valueOf(dto.getType().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid user type: " + dto.getType());
                }
            }
            
            // Mise à jour du mot de passe si fourni
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            
            // Mise à jour du statut activé/désactivé
            if (dto.getEnabled() != null) {
                user.setEnabled(dto.getEnabled());
            }
            
            // Sauvegarde et retour
            User updatedUser = userRepository.save(user);
            log.info("User updated successfully: {}", updatedUser.getId());
            return modelMapper.map(updatedUser, UserResponseDto.class);
            
        } catch (Exception e) {
            log.error("Error updating user ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }
   
    @Transactional
    public void delete(Long id) {
        try {
            log.info("Deleting user ID: {}", id);
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

            // 1. Suppression des dépendances
            reviewRepository.deleteAllByUserId(id);
            subscriptionRepository.deleteAllByUserId(id);

            // 2. Gestion des événements
            eventRepository.findByParticipantsId(id).forEach(event -> {
                event.getParticipants().removeIf(participant -> participant.getId().equals(id));
                eventRepository.save(event);
            });

            // Suppression finale
            userRepository.delete(user);
            log.info("User deleted successfully: {}", id);
            
        } catch (Exception e) {
            log.error("Error deleting user ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Override
    public UserResponseDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return modelMapper.map(user, UserResponseDto.class);
    }

    @Override
    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDto toggleStatus(Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
            
            log.info("Toggling status for user ID: {}, Current status: {}", id, user.getEnabled());
            
            // Initialisation si null
            if (user.getEnabled() == null) {
                user.setEnabled(false);
            }
            
            // Basculement du statut
            user.setEnabled(!user.getEnabled());
            
            User updated = userRepository.save(user);
            log.info("User status toggled successfully. New status: {}", updated.getEnabled());
            
            return modelMapper.map(updated, UserResponseDto.class);
            
        } catch (Exception e) {
            log.error("Error toggling status for user ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to toggle user status: " + e.getMessage(), e);
        }
    }
}
package com.coworking.backend.service;

import com.coworking.backend.dto.EvenementDTO;
import com.coworking.backend.dto.ParticipantDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.*;
import com.coworking.backend.model.Paiement.PaiementStatut;
import com.coworking.backend.model.Paiement.PaiementType;
import com.coworking.backend.repository.EvenementRepository;
import com.coworking.backend.repository.PaiementRepository;
import com.coworking.backend.repository.UserRepository;
import com.coworking.backend.repository.EspacePriveRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final UserRepository userRepository;
    private final EspacePriveRepository espacePriveRepository;
    private final ModelMapper modelMapper;
    private final PaiementRepository paiementRepository;

    public List<EvenementDTO> getAllEvenements() {
        return evenementRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public EvenementDTO getEvenementById(Long id) {
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evenement not found"));
        return convertToDto(evenement);
    }

    private EvenementDTO convertToDto(Evenement evenement) {
        EvenementDTO dto = modelMapper.map(evenement, EvenementDTO.class);
        
        if (evenement.getEspace() != null) {
            dto.setEspaceId(evenement.getEspace().getId());
            dto.setEspaceName(evenement.getEspace().getName());
        }
        
        dto.setParticipants(evenement.getParticipants().stream()
                .map(user -> {
                    ParticipantDTO participant = new ParticipantDTO();
                    participant.setUserId(user.getId());
                    participant.setUserFirstName(user.getFirstName());
                    participant.setUserLastName(user.getLastName());
                    participant.setUserEmail(user.getEmail());
                    participant.setUserPhone(user.getPhone());
                    return participant;
                })
                .collect(Collectors.toList()));
        
        return dto;
    }

    @Transactional
    public EvenementDTO createEvenement(EvenementDTO evenementDTO) {
        EspacePrive espace = espacePriveRepository.findById(evenementDTO.getEspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("EspacePrive not found"));
        
        Evenement evenement = modelMapper.map(evenementDTO, Evenement.class);
        evenement.setEspace(espace);
        evenement.setActive(true);
        
        Evenement savedEvenement = evenementRepository.save(evenement);
        return convertToDto(savedEvenement);
    }

    @Transactional
    public EvenementDTO updateEvenement(Long id, EvenementDTO evenementDTO) {
        Evenement existingEvenement = evenementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evenement not found"));
        
        modelMapper.map(evenementDTO, existingEvenement);
        Evenement updatedEvenement = evenementRepository.save(existingEvenement);
        
        return convertToDto(updatedEvenement);
    }

    @Transactional
    public void deleteEvenement(Long id) {
        if (!evenementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evenement not found");
        }
        evenementRepository.deleteById(id);
    }

    @Transactional
    public EvenementDTO registerParticipant(Long evenementId, Long userId) {
        // Chargement optimisé avec les participants
        Evenement evenement = evenementRepository.findByIdWithParticipants(evenementId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérification d'inscription existante
        boolean alreadyRegistered = evenement.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(userId));
        
        if (alreadyRegistered) {
            throw new IllegalStateException("Vous êtes déjà inscrit à cet événement");
        }

        // Vérification de capacité si applicable
        if (evenement.getMaxParticipants() != null && 
            evenement.getParticipants().size() >= evenement.getMaxParticipants()) {
            throw new IllegalStateException("L'événement est complet");
        }

        // Création et enregistrement du paiement
        Paiement paiement = new Paiement();
        paiement.setMontant(evenement.getPrice());
        paiement.setStatut(PaiementStatut.EN_ATTENTE);
        paiement.setType(PaiementType.EVENEMENT);
        paiement.setDate(LocalDateTime.now());
        paiement.setEvenement(evenement);
        paiement.setUser(user);
        
        // Ajout du participant
        evenement.getParticipants().add(user);
        
        try {
            paiementRepository.save(paiement);
            evenementRepository.save(evenement);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Erreur lors de l'inscription à l'événement");
        }
        
        return convertToDto(evenement);
    }
    
    @Transactional
    public EvenementDTO cancelParticipation(Long evenementId, Long userId) {
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new ResourceNotFoundException("Evenement not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!evenement.getParticipants().contains(user)) {
            throw new IllegalStateException("User is not registered for this event");
        }
        
        evenement.getParticipants().remove(user);
        Evenement updatedEvenement = evenementRepository.save(evenement);
        
        return convertToDto(updatedEvenement);
    }
}
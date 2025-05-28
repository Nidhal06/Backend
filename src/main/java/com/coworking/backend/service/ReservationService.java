package com.coworking.backend.service;

import com.coworking.backend.dto.ReservationDTO;
import com.coworking.backend.exception.AbonnementRequiredException;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.exception.UnavailableException;
import com.coworking.backend.model.*;
import com.coworking.backend.model.Reservation.ReservationStatut;
import com.coworking.backend.model.Paiement.PaiementStatut;
import com.coworking.backend.model.Paiement.PaiementType;
import com.coworking.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final EspaceRepository espaceRepository;
    private final IndisponibiliteRepository indisponibiliteRepository;
    private final PaiementRepository paiementRepository;
    private final AbonnementRepository abonnementRepository;

    @Transactional(readOnly = true)
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        return convertToDto(reservation);
    }
    
    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsBySpace(Long spaceId) {
        return reservationRepository.findByEspaceId(spaceId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        // Validation
        if (reservationDTO.getEspaceId() == null || reservationDTO.getUserId() == null) {
            throw new IllegalArgumentException("Espace ID and User ID must not be null");
        }

        User user = userRepository.findById(reservationDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Espace espace = espaceRepository.findById(reservationDTO.getEspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("Espace not found"));

        Paiement paiement = null;
        boolean paiementValide = false;
        double montant = 0.0;

        if (espace.getType() == Espace.EspaceType.OUVERT) {
            // Gestion espace ouvert
            Abonnement abonnement = abonnementRepository.findActiveByUserAndEspace(user.getId(), espace.getId())
                    .orElseThrow(() -> new AbonnementRequiredException("Active subscription required"));
            
            montant = abonnement.getPrix();
            paiementValide = abonnement.getPaiement() != null && 
                            abonnement.getPaiement().getStatut() == PaiementStatut.VALIDE;
            paiement = abonnement.getPaiement();
            
            // Forcer la mise à jour du DTO
            reservationDTO.setPaiementMontant(montant);
            reservationDTO.setPaiementValide(paiementValide);
        } else {
            // Gestion espace privé
            if (reservationDTO.getPaiementMontant() == null || reservationDTO.getPaiementMontant() <= 0) {
                throw new IllegalArgumentException("Payment amount must be positive");
            }
            montant = reservationDTO.getPaiementMontant();
            paiementValide = reservationDTO.getPaiementValide() != null && reservationDTO.getPaiementValide();
            
            paiement = new Paiement();
            paiement.setMontant(montant);
            paiement.setStatut(paiementValide ? PaiementStatut.VALIDE : PaiementStatut.EN_ATTENTE);
            paiement.setType(PaiementType.RESERVATION);
            paiement.setDate(LocalDateTime.now());
            paiement = paiementRepository.save(paiement);
        }

        // Vérification disponibilité
        if (!isEspaceAvailable(espace.getId(), reservationDTO.getDateDebut(), reservationDTO.getDateFin())) {
            throw new UnavailableException("Space not available for selected dates");
        }

        // Création réservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEspace(espace);
        reservation.setDateDebut(reservationDTO.getDateDebut());
        reservation.setDateFin(reservationDTO.getDateFin());
        reservation.setStatut(ReservationStatut.EN_ATTENTE);
        reservation.setPaiement(paiement);

        Reservation savedReservation = reservationRepository.save(reservation);

        // Pour les espaces privés, lier la réservation au paiement
        if (espace.getType() == Espace.EspaceType.PRIVE) {
            paiement.setReservation(savedReservation);
            paiementRepository.save(paiement);
        }

        // Retourner le DTO converti
        return convertToDto(savedReservation);
    }

    private ReservationDTO convertToDto(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        
        // User info
        if (reservation.getUser() != null) {
            dto.setUserId(reservation.getUser().getId());
            dto.setUserFirstName(reservation.getUser().getFirstName());
            dto.setUserLastName(reservation.getUser().getLastName());
            dto.setUserEmail(reservation.getUser().getEmail());
            dto.setUserPhone(reservation.getUser().getPhone());
        }
        
        // Espace info
        if (reservation.getEspace() != null) {
            dto.setEspaceId(reservation.getEspace().getId());
            dto.setEspaceName(reservation.getEspace().getName());
            dto.setEspaceType(reservation.getEspace().getType().toString());
            
            // Pour les espaces ouverts, récupérer le prix de l'abonnement
            if (reservation.getEspace().getType() == Espace.EspaceType.OUVERT && reservation.getUser() != null) {
                Optional<Abonnement> abonnement = abonnementRepository.findActiveByUserAndEspace(
                    reservation.getUser().getId(), 
                    reservation.getEspace().getId()
                );
                
                if (abonnement.isPresent() && abonnement.get().getPaiement() != null) {
                    dto.setPaiementMontant(abonnement.get().getPrix());
                    dto.setPaiementValide(abonnement.get().getPaiement().getStatut() == PaiementStatut.VALIDE);
                }
            }
        }
        
        // Dates et statut
        dto.setDateDebut(reservation.getDateDebut());
        dto.setDateFin(reservation.getDateFin());
        dto.setStatut(reservation.getStatut().toString());
        
        // Pour les espaces privés, utiliser le paiement direct
        if (reservation.getEspace() != null && 
            reservation.getEspace().getType() == Espace.EspaceType.PRIVE && 
            reservation.getPaiement() != null) {
            dto.setPaiementMontant(reservation.getPaiement().getMontant());
            dto.setPaiementValide(reservation.getPaiement().getStatut() == PaiementStatut.VALIDE);
        }
        
        return dto;
    }
    
    
    @Transactional
    public ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        
        // Update dates
        existingReservation.setDateDebut(reservationDTO.getDateDebut());
        existingReservation.setDateFin(reservationDTO.getDateFin());
        
        // Update payment
        if (existingReservation.getPaiement() != null) {
            existingReservation.getPaiement().setMontant(reservationDTO.getPaiementMontant());
            existingReservation.getPaiement().setStatut(
                reservationDTO.getPaiementValide() ? PaiementStatut.VALIDE : PaiementStatut.EN_ATTENTE
            );
        } else {
            Paiement paiement = new Paiement();
            paiement.setMontant(reservationDTO.getPaiementMontant());
            paiement.setStatut(
                reservationDTO.getPaiementValide() ? PaiementStatut.VALIDE : PaiementStatut.EN_ATTENTE
            );
            paiement.setType(Paiement.PaiementType.RESERVATION);
            paiement.setDate(LocalDateTime.now());
            existingReservation.setPaiement(paiement);
        }
        
        return convertToDto(reservationRepository.save(existingReservation));
    }

    @Transactional
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation not found");
        }
        reservationRepository.deleteById(id);
    }

    private boolean isEspaceAvailable(Long espaceId, LocalDateTime start, LocalDateTime end) {
        // Check for overlapping reservations
        List<Reservation> overlappingReservations = reservationRepository
                .findByEspaceIdAndDateDebutBetweenOrDateFinBetween(
                        espaceId, start, end, start, end);
        
        if (!overlappingReservations.isEmpty()) {
            System.out.println("Found overlapping reservations: " + overlappingReservations);
            return false;
        }
        
        // Check for unavailability periods
        List<Indisponibilite> indisponibilites = indisponibiliteRepository
                .findByEspaceIdAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
                        espaceId, end, start);
        
        if (!indisponibilites.isEmpty()) {
            System.out.println("Found unavailability periods: " + indisponibilites);
            return false;
        }
        
        return true;
    }

    
}
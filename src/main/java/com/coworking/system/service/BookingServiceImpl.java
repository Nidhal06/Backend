package com.coworking.system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.system.dto.*;
import com.coworking.system.entity.*;
import com.coworking.system.exception.*;
import com.coworking.system.repository.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final OpenSpaceRepository openSpaceRepository;
    private final PrivateSpaceRepository privateSpaceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UnavailabilityPeriodRepository unavailabilityPeriodRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingDto dto) {
        validateBookingRequest(dto);
        
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.getUserId()));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setSpecialRequirements(dto.getSpecialRequirements());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        // Set space based on type
        if (Boolean.TRUE.equals(dto.getIsPrivateSpace())) {
            PrivateSpace space = privateSpaceRepository.findById(dto.getSpaceId())
                    .orElseThrow(() -> new ResourceNotFoundException("PrivateSpace", "id", dto.getSpaceId()));
            booking.setPrivateSpace(space);
            booking.setOpenSpace(null);
        } else {
            OpenSpace space = openSpaceRepository.findById(dto.getSpaceId())
                    .orElseThrow(() -> new ResourceNotFoundException("OpenSpace", "id", dto.getSpaceId()));
            booking.setOpenSpace(space);
            booking.setPrivateSpace(null);
            
            // Validate subscription for open spaces
            if (!Boolean.TRUE.equals(dto.getIsPrivateSpace())) {
                if (dto.getSubscriptionId() == null) {
                    throw new BadRequestException("Un abonnement valide est requis pour les espaces ouverts");
                }
                
                Subscription subscription = subscriptionRepository.findById(dto.getSubscriptionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", dto.getSubscriptionId()));
                    
                if (!subscription.getIsActive() || subscription.getEndDate().isBefore(LocalDate.now())) {
                    throw new BadRequestException("L'abonnement n'est pas valide ou a expiré");
                }

            }
        }

        booking.setTotalPrice(calculatePrice(booking));
        Booking savedBooking = bookingRepository.save(booking);
        
        log.info("Booking created: {}", savedBooking.getId());
        return modelMapper.map(savedBooking, BookingResponseDto.class);
    }

    private void validateBookingRequest(BookingDto dto) {
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }
        
        if (dto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Booking start time must be in the future");
        }
        
        if (!isSpaceAvailable(dto)) {
            throw new BadRequestException("Space is not available for the selected period");
        }
    }

    @Override
    public boolean isSpaceAvailable(BookingDto dto) {
        // Vérification des périodes d'indisponibilité
        if (Boolean.TRUE.equals(dto.getIsPrivateSpace())) {
            if (unavailabilityPeriodRepository.existsForPrivateSpaceDuringPeriod(
                dto.getSpaceId(), dto.getStartTime(), dto.getEndTime())) {
                return false;
            }
        } else {
            if (unavailabilityPeriodRepository.existsForOpenSpaceDuringPeriod(
                dto.getSpaceId(), dto.getStartTime(), dto.getEndTime())) {
                return false;
            }
        }
        
        // Vérification des réservations existantes
        return !bookingRepository.existsConflictingBookings(
            dto.getSpaceId(), 
            Boolean.TRUE.equals(dto.getIsPrivateSpace()),
            dto.getStartTime(), 
            dto.getEndTime()
        );
    }

    private double calculatePrice(Booking booking) {
        long hours = Duration.between(booking.getStartTime(), booking.getEndTime()).toHours();
        
        if (booking.getPrivateSpace() != null) {
            // Calculate price for private space
            if (hours <= 4) {
                return booking.getPrivateSpace().getPricePerHour() * hours;
            } else {
                return booking.getPrivateSpace().getPricePerDay();
            }
        } else if (booking.getOpenSpace() != null && booking.getSubscription() != null) {
            // Open space with subscription - price included in subscription
            return 0.0;
        } else {
            // Open space without subscription - hourly rate
            return 10.0 * hours; // Default hourly rate for open space
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        
        if (!canCancelBooking(booking)) {
            throw new BadRequestException("Cancellation is not allowed less than 24 hours before booking start");
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        log.info("Booking cancelled: {}", id);
    }

    private boolean canCancelBooking(Booking booking) {
        return Duration.between(LocalDateTime.now(), booking.getStartTime()).toHours() >= 24;
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return bookingRepository.findByUser(user).stream()
                .map(booking -> {
                    BookingResponseDto dto = modelMapper.map(booking, BookingResponseDto.class);
                    if (booking.getPrivateSpace() != null) {
                        dto.setPrivateSpace(modelMapper.map(booking.getPrivateSpace(), PrivateSpaceDto.class));
                    } else if (booking.getOpenSpace() != null) {
                        dto.setOpenSpace(modelMapper.map(booking.getOpenSpace(), OpenSpaceDto.class));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getSpaceBookings(Long spaceId, boolean isPrivate) {
        if (isPrivate) {
            PrivateSpace space = privateSpaceRepository.findById(spaceId)
                    .orElseThrow(() -> new ResourceNotFoundException("PrivateSpace", "id", spaceId));
            return bookingRepository.findByPrivateSpace(space).stream()
                    .map(booking -> modelMapper.map(booking, BookingResponseDto.class))
                    .collect(Collectors.toList());
        } else {
            OpenSpace space = openSpaceRepository.findById(spaceId)
                    .orElseThrow(() -> new ResourceNotFoundException("OpenSpace", "id", spaceId));
            return bookingRepository.findByOpenSpace(space).stream()
                    .map(booking -> modelMapper.map(booking, BookingResponseDto.class))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public BookingResponseDto getBookingDetails(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        
        BookingResponseDto response = modelMapper.map(booking, BookingResponseDto.class);
        
        if (booking.getPrivateSpace() != null) {
            response.setPrivateSpace(modelMapper.map(booking.getPrivateSpace(), PrivateSpaceDto.class));
        } else if (booking.getOpenSpace() != null) {
            response.setOpenSpace(modelMapper.map(booking.getOpenSpace(), OpenSpaceDto.class));
        }
        
        if (booking.getSubscription() != null) {
            response.setSubscription(modelMapper.map(booking.getSubscription(), SubscriptionResponseDto.class));
        }
        
        return response;
    }

    @Override
    @Transactional
    public BookingResponseDto updateBooking(Long id, BookingUpdateDto dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        
        if (!booking.getStatus().equals(Booking.BookingStatus.CONFIRMED)) {
            throw new BadRequestException("Only confirmed bookings can be modified");
        }
        
        if (dto.getStartTime() != null) booking.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) booking.setEndTime(dto.getEndTime());
        if (dto.getSpecialRequirements() != null) {
            booking.setSpecialRequirements(dto.getSpecialRequirements());
        }
        
        // Recalculate price if times changed
        if (dto.getStartTime() != null || dto.getEndTime() != null) {
            booking.setTotalPrice(calculatePrice(booking));
        }
        
        Booking updated = bookingRepository.save(booking);
        return modelMapper.map(updated, BookingResponseDto.class);
    }
}
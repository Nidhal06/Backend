package com.coworking.system.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.system.dto.BookingResponseDto;
import com.coworking.system.entity.Booking;
import com.coworking.system.exception.ResourceNotFoundException;
import com.coworking.system.repository.BookingRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminBookingServiceImpl implements AdminBookingService {
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(booking -> modelMapper.map(booking, BookingResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> searchBookings(Long userId, Long spaceId, Boolean isPrivate) {
        List<Booking> bookings;
        
        if (userId != null && spaceId != null) {
            bookings = bookingRepository.findByUserIdAndSpaceId(userId, spaceId, isPrivate);
        } else if (userId != null) {
            bookings = bookingRepository.findByUserId(userId);
        } else if (spaceId != null) {
            bookings = isPrivate ? 
                bookingRepository.findByPrivateSpaceId(spaceId) :
                bookingRepository.findByOpenSpaceId(spaceId);
        } else {
            bookings = bookingRepository.findAll();
        }
        
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBookingAsAdmin(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);
        
        return modelMapper.map(updated, BookingResponseDto.class);
    }
}
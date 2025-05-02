package com.coworking.system.service;

import com.coworking.system.dto.BookingResponseDto;
import java.util.List;

public interface AdminBookingService {
    List<BookingResponseDto> getAllBookings();
    List<BookingResponseDto> searchBookings(Long userId, Long spaceId, Boolean isPrivate);
    BookingResponseDto cancelBookingAsAdmin(Long id);
}
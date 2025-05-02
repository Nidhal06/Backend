package com.coworking.system.service;

import java.util.List;
import com.coworking.system.dto.BookingDto;
import com.coworking.system.dto.BookingResponseDto;
import com.coworking.system.dto.BookingUpdateDto;

public interface BookingService {
    BookingResponseDto createBooking(BookingDto dto);
    void cancelBooking(Long id);
    List<BookingResponseDto> getUserBookings(Long userId);
    List<BookingResponseDto> getSpaceBookings(Long spaceId, boolean isPrivate);
    BookingResponseDto getBookingDetails(Long id); 
    BookingResponseDto updateBooking(Long id, BookingUpdateDto dto);
    boolean isSpaceAvailable(BookingDto dto);
}

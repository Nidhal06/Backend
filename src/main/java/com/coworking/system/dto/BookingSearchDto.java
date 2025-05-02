package com.coworking.system.dto;

import java.time.LocalDateTime;
import com.coworking.system.entity.Booking;
import lombok.Data;

@Data
public class BookingSearchDto {
    private Long userId;
    private Long spaceId;
    private Boolean isPrivate;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Booking.BookingStatus status;
}

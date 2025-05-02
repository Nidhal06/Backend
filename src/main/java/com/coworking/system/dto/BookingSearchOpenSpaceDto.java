package com.coworking.system.dto;

import java.time.LocalDateTime;
import com.coworking.system.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingSearchOpenSpaceDto {
	
	private Long openSpaceId;
    private Long userId;
    private Booking.BookingStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}

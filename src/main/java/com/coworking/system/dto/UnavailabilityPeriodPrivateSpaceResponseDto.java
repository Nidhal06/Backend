package com.coworking.system.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UnavailabilityPeriodPrivateSpaceResponseDto {
	
	private Long id;
    private PrivateSpaceDto privateSpace;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;

}

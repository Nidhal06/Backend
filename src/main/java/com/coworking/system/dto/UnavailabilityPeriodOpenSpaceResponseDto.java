package com.coworking.system.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UnavailabilityPeriodOpenSpaceResponseDto {
	private Long id;
    private OpenSpaceDto openSpace;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;

}

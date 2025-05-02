package com.coworking.system.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnavailabilityPeriodOpenSpaceDto {
	
	@NotNull
    private Long openSpaceId;

    @NotNull
    @Future
    private LocalDateTime startTime;
    
    @NotNull
    @Future
    private LocalDateTime endTime;
    
    @NotBlank
    private String reason;

}

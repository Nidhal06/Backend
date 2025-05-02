package com.coworking.system.service;

import com.coworking.system.dto.UnavailabilityPeriodDto;
import com.coworking.system.dto.UnavailabilityPeriodOpenSpaceResponseDto;
import com.coworking.system.dto.UnavailabilityPeriodPrivateSpaceResponseDto;
import com.coworking.system.entity.UnavailabilityPeriod;

import java.util.List;

public interface UnavailabilityPeriodService {
    UnavailabilityPeriodOpenSpaceResponseDto createForOpenSpace(UnavailabilityPeriodDto dto);
    UnavailabilityPeriodPrivateSpaceResponseDto createForPrivateSpace(UnavailabilityPeriodDto dto);
    List<UnavailabilityPeriodOpenSpaceResponseDto> getOpenSpaceUnavailabilityPeriods(Long openSpaceId);
    List<UnavailabilityPeriodPrivateSpaceResponseDto> getPrivateSpaceUnavailabilityPeriods(Long privateSpaceId);
    void delete(Long id);
    
    // Add these helper methods
    UnavailabilityPeriodOpenSpaceResponseDto mapToOpenSpaceResponseDto(UnavailabilityPeriod period);
    UnavailabilityPeriodPrivateSpaceResponseDto mapToPrivateSpaceResponseDto(UnavailabilityPeriod period);
}
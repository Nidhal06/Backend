package com.coworking.system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.system.dto.*;
import com.coworking.system.entity.*;
import com.coworking.system.exception.ResourceNotFoundException;
import com.coworking.system.repository.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnavailabilityPeriodServiceImpl implements UnavailabilityPeriodService {
    private final UnavailabilityPeriodRepository repository;
    private final OpenSpaceRepository openSpaceRepository;
    private final PrivateSpaceRepository privateSpaceRepository;

    @Override
    @Transactional
    public UnavailabilityPeriodOpenSpaceResponseDto createForOpenSpace(UnavailabilityPeriodDto dto) {
        OpenSpace openSpace = openSpaceRepository.findById(dto.getOpenSpaceId())
                .orElseThrow(() -> new ResourceNotFoundException("OpenSpace", "id", dto.getOpenSpaceId()));
        
        UnavailabilityPeriod period = new UnavailabilityPeriod();
        period.setOpenSpace(openSpace);
        period.setPrivateSpace(null);
        period.setStartTime(dto.getStartTime());
        period.setEndTime(dto.getEndTime());
        period.setReason(dto.getReason());
        period.setIsActive(true);
        
        UnavailabilityPeriod saved = repository.save(period);
        return mapToOpenSpaceResponseDto(saved);
    }

    @Override
    @Transactional
    public UnavailabilityPeriodPrivateSpaceResponseDto createForPrivateSpace(UnavailabilityPeriodDto dto) {
        PrivateSpace privateSpace = privateSpaceRepository.findById(dto.getPrivateSpaceId())
                .orElseThrow(() -> new ResourceNotFoundException("PrivateSpace", "id", dto.getPrivateSpaceId()));
        
        UnavailabilityPeriod period = new UnavailabilityPeriod();
        period.setPrivateSpace(privateSpace);
        period.setOpenSpace(null);
        period.setStartTime(dto.getStartTime());
        period.setEndTime(dto.getEndTime());
        period.setReason(dto.getReason());
        period.setIsActive(true);
        
        UnavailabilityPeriod saved = repository.save(period);
        return mapToPrivateSpaceResponseDto(saved);
    }

    @Override
    public List<UnavailabilityPeriodOpenSpaceResponseDto> getOpenSpaceUnavailabilityPeriods(Long openSpaceId) {
        return repository.findByOpenSpaceId(openSpaceId).stream()
                .map(this::mapToOpenSpaceResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UnavailabilityPeriodPrivateSpaceResponseDto> getPrivateSpaceUnavailabilityPeriods(Long privateSpaceId) {
        return repository.findByPrivateSpaceId(privateSpaceId).stream()
                .map(this::mapToPrivateSpaceResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        UnavailabilityPeriod period = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UnavailabilityPeriod", "id", id));
        repository.delete(period);
    }

    @Override
    public UnavailabilityPeriodOpenSpaceResponseDto mapToOpenSpaceResponseDto(UnavailabilityPeriod period) {
        UnavailabilityPeriodOpenSpaceResponseDto dto = new UnavailabilityPeriodOpenSpaceResponseDto();
        dto.setId(period.getId());
        dto.setStartTime(period.getStartTime());
        dto.setEndTime(period.getEndTime());
        dto.setReason(period.getReason());
        
        if (period.getOpenSpace() != null) {
            OpenSpaceDto spaceDto = new OpenSpaceDto();
            spaceDto.setId(period.getOpenSpace().getId());
            spaceDto.setName(period.getOpenSpace().getName());
            spaceDto.setDescription(period.getOpenSpace().getDescription());
            spaceDto.setCapacity(period.getOpenSpace().getCapacity());
            spaceDto.setLocation(period.getOpenSpace().getLocation());
            spaceDto.setIsActive(period.getOpenSpace().getIsActive());
            dto.setOpenSpace(spaceDto);
        }
        
        return dto;
    }

    @Override
    public UnavailabilityPeriodPrivateSpaceResponseDto mapToPrivateSpaceResponseDto(UnavailabilityPeriod period) {
        UnavailabilityPeriodPrivateSpaceResponseDto dto = new UnavailabilityPeriodPrivateSpaceResponseDto();
        dto.setId(period.getId());
        dto.setStartTime(period.getStartTime());
        dto.setEndTime(period.getEndTime());
        dto.setReason(period.getReason());
        
        if (period.getPrivateSpace() != null) {
            PrivateSpaceDto spaceDto = new PrivateSpaceDto();
            spaceDto.setId(period.getPrivateSpace().getId());
            spaceDto.setName(period.getPrivateSpace().getName());
            spaceDto.setDescription(period.getPrivateSpace().getDescription());
            spaceDto.setCapacity(period.getPrivateSpace().getCapacity());
            spaceDto.setLocation(period.getPrivateSpace().getLocation());
            spaceDto.setPricePerHour(period.getPrivateSpace().getPricePerHour());
            spaceDto.setPricePerDay(period.getPrivateSpace().getPricePerDay());
            spaceDto.setIsActive(period.getPrivateSpace().getIsActive());
            dto.setPrivateSpace(spaceDto);
        }
        
        return dto;
    }

}
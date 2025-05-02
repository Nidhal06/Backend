package com.coworking.system.service;

import java.util.List;
import com.coworking.system.dto.OpenSpaceDto;
import com.coworking.system.dto.OpenSpaceResponseDto;

public interface AdminOpenSpaceService {
    OpenSpaceResponseDto create(OpenSpaceDto dto);
    OpenSpaceResponseDto update(Long id, OpenSpaceDto dto);
    void delete(Long id);
    OpenSpaceResponseDto getById(Long id);
    List<OpenSpaceResponseDto> getAll();
    OpenSpaceResponseDto toggleStatus(Long id);
}
package com.coworking.system.service;

import org.springframework.web.multipart.MultipartFile;
import com.coworking.system.dto.PrivateSpaceDto;
import com.coworking.system.dto.PrivateSpaceResponseDto;
import java.io.IOException;
import java.util.List;


public interface AdminSpaceService {
    PrivateSpaceResponseDto create(PrivateSpaceDto dto, MultipartFile photo, MultipartFile[] gallery) throws IOException;
    PrivateSpaceResponseDto update(Long id, PrivateSpaceDto dto, MultipartFile photo, MultipartFile[] gallery) throws IOException;
    void delete(Long id);
    PrivateSpaceResponseDto getById(Long id);
    List<PrivateSpaceResponseDto> getAll();
    PrivateSpaceResponseDto toggleStatus(Long id);
}
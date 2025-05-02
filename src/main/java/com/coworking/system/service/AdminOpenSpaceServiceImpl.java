package com.coworking.system.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.system.dto.OpenSpaceDto;
import com.coworking.system.dto.OpenSpaceResponseDto;
import com.coworking.system.entity.OpenSpace;
import com.coworking.system.exception.ResourceNotFoundException;
import com.coworking.system.repository.OpenSpaceRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOpenSpaceServiceImpl implements AdminOpenSpaceService {
    private final OpenSpaceRepository repository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public OpenSpaceResponseDto create(OpenSpaceDto dto) {
        OpenSpace openSpace = modelMapper.map(dto, OpenSpace.class);
        OpenSpace saved = repository.save(openSpace);
        return modelMapper.map(saved, OpenSpaceResponseDto.class);
    }

    @Override
    @Transactional
    public OpenSpaceResponseDto update(Long id, OpenSpaceDto dto) {
        OpenSpace openSpace = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OpenSpace", "id", id));
        modelMapper.map(dto, openSpace);
        OpenSpace updated = repository.save(openSpace);
        return modelMapper.map(updated, OpenSpaceResponseDto.class);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        OpenSpace openSpace = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OpenSpace", "id", id));
        repository.delete(openSpace);
    }

    @Override
    public OpenSpaceResponseDto getById(Long id) {
        OpenSpace openSpace = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OpenSpace", "id", id));
        return modelMapper.map(openSpace, OpenSpaceResponseDto.class);
    }

    @Override
    public List<OpenSpaceResponseDto> getAll() {
        return repository.findAll().stream()
                .map(space -> modelMapper.map(space, OpenSpaceResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OpenSpaceResponseDto toggleStatus(Long id) {
        OpenSpace openSpace = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OpenSpace", "id", id));
        openSpace.setIsActive(!openSpace.getIsActive());
        OpenSpace updated = repository.save(openSpace);
        return modelMapper.map(updated, OpenSpaceResponseDto.class);
    }
}
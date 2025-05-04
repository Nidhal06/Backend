package com.coworking.system.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.coworking.system.dto.AmenityDto;
import com.coworking.system.dto.PrivateSpaceDto;
import com.coworking.system.dto.PrivateSpaceResponseDto;
import com.coworking.system.entity.Amenity;
import com.coworking.system.entity.PrivateSpace;
import com.coworking.system.exception.BadRequestException;
import com.coworking.system.exception.ResourceNotFoundException;
import com.coworking.system.repository.AmenityRepository;
import com.coworking.system.repository.PrivateSpaceRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminSpaceServiceImpl implements AdminSpaceService {
    private final PrivateSpaceRepository repository;
    private final AmenityRepository amenityRepository;
    private final ModelMapper modelMapper;
    private final Path uploadLocation;

    @Override
    @Transactional
    public PrivateSpaceResponseDto create(PrivateSpaceDto dto, MultipartFile photo, MultipartFile[] gallery) throws IOException {
        if (photo == null || photo.isEmpty()) {
            throw new BadRequestException("Main photo is required");
        }

        PrivateSpace space = modelMapper.map(dto, PrivateSpace.class);
        space.setIsActive(true);

        PrivateSpace savedSpace = repository.save(space);
        
     // Ajouter les amenities
        if (dto.getAmenityIds() != null && !dto.getAmenityIds().isEmpty()) {
            Set<Amenity> amenities = dto.getAmenityIds().stream()
                .map(id -> amenityRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", id)))
                .collect(Collectors.toSet());
            savedSpace.setAmenities(amenities);
        }

        try {
            String photoFilename = saveFile(photo);
            savedSpace.setPhoto("/uploads/" + photoFilename);

            Set<String> galleryPaths = new HashSet<>();
            if (gallery != null) {
                for (MultipartFile file : gallery) {
                    if (!file.isEmpty()) {
                        String filename = saveFile(file);
                        galleryPaths.add("/uploads/" + filename);
                    }
                }
            }
            savedSpace.setGallery(galleryPaths);

            PrivateSpace finalSpace = repository.save(savedSpace);
            return modelMapper.map(finalSpace, PrivateSpaceResponseDto.class);

        } catch (IOException ex) {
            repository.delete(savedSpace);
            throw new RuntimeException("Failed to store files: " + ex.getMessage(), ex);
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;
        Path targetLocation = uploadLocation.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }
    
    @Transactional
    public PrivateSpaceResponseDto update(Long id, PrivateSpaceDto dto, MultipartFile photo, MultipartFile[] gallery) throws IOException {
        PrivateSpace space = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PrivateSpace", "id", id));
        
        // Update basic fields
        if (dto.getName() != null) space.setName(dto.getName());
        if (dto.getDescription() != null) space.setDescription(dto.getDescription());
        if (dto.getCapacity() != null) space.setCapacity(dto.getCapacity());
        if (dto.getPricePerHour() != null) space.setPricePerHour(dto.getPricePerHour());
        if (dto.getPricePerDay() != null) space.setPricePerDay(dto.getPricePerDay());
        if (dto.getIsActive() != null) space.setIsActive(dto.getIsActive());

        // Handle amenities
        if (dto.getAmenityIds() != null) {
            Set<Amenity> amenities = dto.getAmenityIds().stream()
                .map(amenityId -> amenityRepository.findById(amenityId)
                    .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", amenityId)))
                .collect(Collectors.toSet());
            space.setAmenities(amenities);
        }

        // Handle main photo update
        if (photo != null && !photo.isEmpty()) {
            String photoFilename = saveFile(photo);
            space.setPhoto("/uploads/" + photoFilename);
        }

        // Handle gallery updates
        if (gallery != null && gallery.length > 0) {
            Set<String> galleryPaths = space.getGallery() != null ? 
                new HashSet<>(space.getGallery()) : new HashSet<>();
            
            for (MultipartFile file : gallery) {
                if (!file.isEmpty()) {
                    String filename = saveFile(file);
                    galleryPaths.add("/uploads/" + filename);
                }
            }
            space.setGallery(galleryPaths);
        }

        PrivateSpace updated = repository.save(space);
        return modelMapper.map(updated, PrivateSpaceResponseDto.class);
    }


    @Override
    @Transactional
    public void delete(Long id) {
    	PrivateSpace space = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PrivateSpace", "id", id));
        repository.delete(space);
    }

    @Override
    public PrivateSpaceResponseDto getById(Long id) {
    	PrivateSpace space = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PrivateSpace", "id", id));
        return modelMapper.map(space, PrivateSpaceResponseDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrivateSpaceResponseDto> getAll() {
        List<PrivateSpace> spaces = repository.findAllWithAmenities();

        return spaces.stream()
        	    .map((Function<PrivateSpace, PrivateSpaceResponseDto>) space -> {
        	        log.info("Mapping space: {}", space.getName());
        	        if (space.getAmenities() != null) {
        	            log.info("Amenities count: {}", space.getAmenities().size());
        	        }
        	        PrivateSpaceResponseDto dto = modelMapper.map(space, PrivateSpaceResponseDto.class);
        	        if (space.getAmenities() != null) {
        	            dto.setAmenities(space.getAmenities().stream()
        	                    .map(amenity -> modelMapper.map(amenity, AmenityDto.class))
        	                    .collect(Collectors.toSet()));
        	        } else {
        	            dto.setAmenities(new HashSet<>());
        	        }
        	        return dto;
        	    })
        	    .collect(Collectors.toList());}



    @Override
    @Transactional
    public PrivateSpaceResponseDto toggleStatus(Long id) {
    	PrivateSpace space = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PrivateSpace", "id", id));

        space.setIsActive(!space.getIsActive());
        PrivateSpace updated = repository.save(space);
        return modelMapper.map(updated, PrivateSpaceResponseDto.class);
    }
}

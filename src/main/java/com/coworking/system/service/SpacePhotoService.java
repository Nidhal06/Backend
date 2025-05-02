package com.coworking.system.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.coworking.system.entity.PrivateSpace;
import com.coworking.system.exception.ResourceNotFoundException;
import com.coworking.system.repository.PrivateSpaceRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class SpacePhotoService {
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    private final PrivateSpaceRepository spaceRepository;

    public SpacePhotoService(PrivateSpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    public String uploadMainPhoto(Long spaceId, MultipartFile file) throws IOException {
        PrivateSpace space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", "id", spaceId));
        
        String path = saveFile(file);
        space.setPhoto(path);
        spaceRepository.save(space);
        return path;
    }

    public String addToGallery(Long spaceId, MultipartFile file) throws IOException {
        PrivateSpace space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", "id", spaceId));
        
        String path = saveFile(file);
        Set<String> gallery = space.getGallery();
        if (gallery == null) {
            gallery = new HashSet<>();
        }
        gallery.add(path);
        space.setGallery(gallery);
        spaceRepository.save(space);
        return path;
    }

    public void removeFromGallery(Long spaceId, String imagePath) {
    	PrivateSpace space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space", "id", spaceId));
        
        Set<String> gallery = space.getGallery();
        if (gallery != null) {
            gallery.remove(imagePath);
            space.setGallery(gallery);
            spaceRepository.save(space);
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + "." + extension;
        Path filePath = uploadPath.resolve(filename);

        Files.copy(file.getInputStream(), filePath);
        return "/uploads/" + filename;
    }
}
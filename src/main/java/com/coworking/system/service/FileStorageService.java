package com.coworking.system.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path uploadLocation;

    public FileStorageService(Path uploadLocation) {
        this.uploadLocation = uploadLocation;
    }

    public String save(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Normaliser le nom du fichier
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path destinationFile = this.uploadLocation.resolve(fileName).normalize();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Tu peux ici retourner seulement le chemin relatif si tu veux
            return fileName;

        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file: " + ex.getMessage(), ex);
        }
    }
}

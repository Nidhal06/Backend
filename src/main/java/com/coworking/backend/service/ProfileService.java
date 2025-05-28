package com.coworking.backend.service;

import com.coworking.backend.dto.ProfilDto;
import com.coworking.backend.dto.ProfileUpdateDTO;
import com.coworking.backend.exception.ResourceNotFoundException;
import com.coworking.backend.model.User;
import com.coworking.backend.repository.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ProfilDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return convertToDto(user);
    }

    public ProfilDto updateUserProfile(String email, ProfileUpdateDTO userProfileUpdateDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (userProfileUpdateDTO.getFirstName() != null) {
            user.setFirstName(userProfileUpdateDTO.getFirstName());
        }
        if (userProfileUpdateDTO.getLastName() != null) {
            user.setLastName(userProfileUpdateDTO.getLastName());
        }
        if (userProfileUpdateDTO.getPhone() != null) {
            user.setPhone(userProfileUpdateDTO.getPhone());
        }
        if (userProfileUpdateDTO.getProfileImagePath() != null) {
            user.setProfileImagePath(userProfileUpdateDTO.getProfileImagePath());
        }

        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    public String updateProfileImage(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID().toString() + "." + extension;
            Path filePath = uploadPath.resolve(filename);

            Files.copy(file.getInputStream(), filePath);

            String relativePath = "/uploads/" + filename;
            user.setProfileImagePath(relativePath);
            userRepository.save(user);

            return relativePath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private ProfilDto convertToDto(User user) {
        return modelMapper.map(user, ProfilDto.class);
    }
}

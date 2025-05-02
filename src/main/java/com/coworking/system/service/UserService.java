package com.coworking.system.service;

import org.springframework.web.multipart.MultipartFile;
import com.coworking.system.dto.UserDto;
import com.coworking.system.dto.UserProfileUpdateDTO;
import java.util.List;

public interface UserService {
    UserDto getCurrentUser(String username);
    UserDto updateUserProfile(String username, UserProfileUpdateDTO userProfileUpdateDTO);
    String updateProfileImage(String username, MultipartFile file);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    void deleteUser(Long id);
    UserDto updateUser(Long id, UserDto userDTO);
}

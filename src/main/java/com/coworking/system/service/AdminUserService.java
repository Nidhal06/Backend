package com.coworking.system.service;

import java.util.List;
import com.coworking.system.dto.UserDto;
import com.coworking.system.dto.UserResponseDto;


public interface AdminUserService {
    UserResponseDto create(UserDto dto);
    UserResponseDto update(Long id, UserDto dto);
    void delete(Long id);
    UserResponseDto getById(Long id);
    List<UserResponseDto> getAll();
    UserResponseDto toggleStatus(Long id);
}

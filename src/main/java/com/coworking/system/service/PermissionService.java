package com.coworking.system.service;

import java.util.List;
import com.coworking.system.dto.PermissionDto;
import com.coworking.system.entity.Permission;

public interface PermissionService {
    Permission createPermission(PermissionDto dto);
    List<Permission> getAllPermissions();
    Permission getPermissionById(Long id);
    void deletePermission(Long id);
}
package com.coworking.system.service;

import java.util.List;
import com.coworking.system.dto.RoleDto;
import com.coworking.system.entity.Role;

public interface RoleService {
    Role createRole(RoleDto dto);
    Role updateRole(Long id, RoleDto dto);
    List<Role> getAllRoles();
    Role getRoleById(Long id);
    void deleteRole(Long id);
    Role addPermissionsToRole(Long roleId, List<Long> permissionIds);
    Role removePermissionFromRole(Long roleId, Long permissionId);
}
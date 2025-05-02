package com.coworking.system.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.system.dto.RoleDto;
import com.coworking.system.entity.Permission;
import com.coworking.system.entity.Role;
import com.coworking.system.exception.ResourceNotFoundException;
import com.coworking.system.repository.PermissionRepository;
import com.coworking.system.repository.RoleRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Role createRole(RoleDto dto) {
        Role role = modelMapper.map(dto, Role.class);
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role updateRole(Long id, RoleDto dto) {
        Role role = getRoleById(id);
        modelMapper.map(dto, role);
        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = getRoleById(id);
        roleRepository.delete(role);
    }

    @Override
    @Transactional
    public Role addPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = getRoleById(roleId);
        Set<Permission> permissions = permissionIds.stream()
            .map(permissionId -> permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId)))
            .collect(Collectors.toSet());

        role.getPermissions().addAll(permissions);
        return roleRepository.save(role);
    }


    @Override
    @Transactional
    public Role removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = getRoleById(roleId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId));
        
        role.getPermissions().remove(permission);
        return roleRepository.save(role);
    }
}
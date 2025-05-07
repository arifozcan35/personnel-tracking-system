package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.entity.Permission;
import com.personneltrackingsystem.entity.Role;
import com.personneltrackingsystem.entity.RolePermission;
import com.personneltrackingsystem.exception.ResourceNotFoundException;
import com.personneltrackingsystem.repository.PermissionRepository;
import com.personneltrackingsystem.repository.RolePermissionRepository;
import com.personneltrackingsystem.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
    }

    @Override
    public Permission getPermissionByName(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with name: " + name));
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission updatePermission(Permission permission) {
        getPermissionById(permission.getId()); // Check if exists
        return permissionRepository.save(permission);
    }

    @Override
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }

    @Override
    public boolean hasPermission(Role role, String resourceName, String httpMethod, String requestPath) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRole(role);
        
        return rolePermissions.stream()
                .map(RolePermission::getPermission)
                .anyMatch(permission -> 
                    permission.getResource().equals(resourceName) && 
                    permission.getMethod().equals(httpMethod) && 
                    pathMatcher.match(permission.getPathPattern(), requestPath)
                );
    }

    @Override
    public List<Permission> getPermissionsByRole(Role role) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRole(role);
        return rolePermissions.stream()
                .map(RolePermission::getPermission)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignPermissionToRole(Role role, Long permissionId) {
        Permission permission = getPermissionById(permissionId);
        
        boolean alreadyAssigned = rolePermissionRepository.findByRole(role)
                .stream()
                .anyMatch(rp -> rp.getPermission().getId().equals(permissionId));
        
        if (!alreadyAssigned) {
            RolePermission rolePermission = RolePermission.builder()
                    .role(role)
                    .permission(permission)
                    .build();
            
            rolePermissionRepository.save(rolePermission);
        }
    }

    @Override
    @Transactional
    public void removePermissionFromRole(Role role, Long permissionId) {
        rolePermissionRepository.findByRole(role)
                .stream()
                .filter(rp -> rp.getPermission().getId().equals(permissionId))
                .forEach(rolePermissionRepository::delete);
    }
} 
package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.entity.Permission;
import com.personneltrackingsystem.entity.Role;
import com.personneltrackingsystem.entity.RolePermission;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.repository.PermissionRepository;
import com.personneltrackingsystem.repository.RolePermissionRepository;
import com.personneltrackingsystem.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    private final RolePermissionRepository rolePermissionRepository;
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    @Override
    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new BaseException(MessageType.PERMISSION_NOT_FOUND, id.toString()));
    }


    @Override
    public Permission getPermissionByName(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new BaseException(MessageType.PERMISSION_NOT_FOUND, name));
    }


    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }


    @Override
    @Transactional
    public Permission createPermission(Permission permission) {
        // Validation
        if (ObjectUtils.isEmpty(permission.getName())) {
            throw new ValidationException(MessageType.PERMISSION_NAME_REQUIRED);
        }
        
        if (ObjectUtils.isEmpty(permission.getResource())) {
            throw new ValidationException(MessageType.PERMISSION_RESOURCE_REQUIRED);
        }
        
        if (ObjectUtils.isEmpty(permission.getMethod())) {
            throw new ValidationException(MessageType.PERMISSION_METHOD_REQUIRED);
        }
        
        if (ObjectUtils.isEmpty(permission.getPathPattern())) {
            throw new ValidationException(MessageType.PERMISSION_PATH_REQUIRED);
        }

        // Check uniqueness
        if (permissionRepository.findByName(permission.getName()).isPresent()) {
            throw new ValidationException(MessageType.PERMISSION_NAME_ALREADY_EXISTS, permission.getName());
        }

        return permissionRepository.save(permission);
    }


    @Override
    @Transactional
    public Permission updatePermission(Permission permission) {
        // Check if permission exists
        Permission existingPermission = getPermissionById(permission.getId());
        
        // Validation for required fields
        if (ObjectUtils.isEmpty(permission.getName())) {
            throw new ValidationException(MessageType.PERMISSION_NAME_REQUIRED);
        }
        
        if (ObjectUtils.isEmpty(permission.getResource())) {
            throw new ValidationException(MessageType.PERMISSION_RESOURCE_REQUIRED);
        }
        
        if (ObjectUtils.isEmpty(permission.getMethod())) {
            throw new ValidationException(MessageType.PERMISSION_METHOD_REQUIRED);
        }
        
        if (ObjectUtils.isEmpty(permission.getPathPattern())) {
            throw new ValidationException(MessageType.PERMISSION_PATH_REQUIRED);
        }

        // Check uniqueness if name is being changed
        if (!existingPermission.getName().equals(permission.getName()) && 
            permissionRepository.findByName(permission.getName()).isPresent()) {
            throw new ValidationException(MessageType.PERMISSION_NAME_ALREADY_EXISTS, permission.getName());
        }
        
        return permissionRepository.save(permission);
    }


    @Override
    @Transactional
    public void deletePermission(Long id) {
        Optional<Permission> optPermission = permissionRepository.findById(id);
        
        if (optPermission.isPresent()) {
            permissionRepository.deleteById(id);
        } else {
            throw new BaseException(MessageType.PERMISSION_NOT_FOUND, id.toString());
        }
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
        
        if (alreadyAssigned) {
            throw new ValidationException(MessageType.PERMISSION_ALREADY_ASSIGNED, 
                "Permission '" + permission.getName() + "' is already assigned to this role");
        }
        
        RolePermission rolePermission = RolePermission.builder()
                .role(role)
                .permission(permission)
                .build();
        
        rolePermissionRepository.save(rolePermission);
    }

    
    @Override
    @Transactional
    public void removePermissionFromRole(Role role, Long permissionId) {
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRole(role)
                .stream()
                .filter(rp -> rp.getPermission().getId().equals(permissionId))
                .collect(Collectors.toList());
        
        if (rolePermissions.isEmpty()) {
            throw new ValidationException(MessageType.PERMISSION_NOT_ASSIGNED, 
                "Permission is not assigned to this role");
        }
        
        rolePermissions.forEach(rolePermissionRepository::delete);
    }
} 
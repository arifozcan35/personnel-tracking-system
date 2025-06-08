package com.personneltrackingsystem.service;

import com.personneltrackingsystem.entity.Permission;
import com.personneltrackingsystem.entity.Role;

import java.util.List;

public interface PermissionService {

    Permission createPermission(Permission permission);

    Permission getPermissionById(Long id);

    List<Permission> getAllPermissions();

    Permission updatePermission(Permission permission);

    void deletePermission(Long id);
    
    
    boolean hasPermission(Role role, String resourceName, String httpMethod, String requestPath);

    List<Permission> getPermissionsByRole(Role role);

    void assignPermissionToRole(Role role, Long permissionId);

    void removePermissionFromRole(Role role, Long permissionId);

} 
package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoRolePermission;
import com.personneltrackingsystem.dto.DtoRolePermissionIU;

import java.util.List;
import java.util.Optional;

public interface RolePermissionService {
    List<DtoRolePermission> getAllRolePermissions();
    Optional<DtoRolePermission> getRolePermissionById(Long id);
    DtoRolePermission getOneRolePermission(Long id);
    DtoRolePermission saveOneRolePermission(DtoRolePermissionIU rolePermissionDto);
    DtoRolePermission updateOneRolePermission(Long id, DtoRolePermissionIU rolePermissionDto);
    void deleteOneRolePermission(Long id);

} 
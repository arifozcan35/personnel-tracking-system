package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.RolePermissionController;
import com.personneltrackingsystem.dto.DtoPermission;
import com.personneltrackingsystem.dto.DtoPermissionIU;
import com.personneltrackingsystem.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RolePermissionControllerImpl implements RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @Override
    public List<DtoPermission> getAllRolePermissions() {
        return rolePermissionService.getAllRolePermissions();
    }

    @Override
    public DtoPermission getOneRolePermission(Long rolePermissionId) {
        return rolePermissionService.getOneRolePermission(rolePermissionId);
    }

    @Override
    public DtoPermission createRolePermission(DtoPermissionIU newRolePermission) {
        return rolePermissionService.createRolePermission(newRolePermission);
    }

    @Override
    public DtoPermission updateRolePermission(Long rolePermissionId, DtoPermissionIU newRolePermission) {
        return rolePermissionService.updateRolePermission(rolePermissionId, newRolePermission);
    }

    @Override
    public void deleteRolePermission(Long rolePermissionId) {
        rolePermissionService.deleteRolePermission(rolePermissionId);
    }
} 
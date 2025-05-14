package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.RoleController;
import com.personneltrackingsystem.dto.DtoPermission;
import com.personneltrackingsystem.dto.DtoPermissionIU;
import com.personneltrackingsystem.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoleControllerImpl implements RoleController {

    private final RoleService roleService;

    @Override
    public List<DtoPermission> getAllRoles() {
        return roleService.getAllRoles();
    }

    @Override
    public DtoPermission getOneRole(Long roleId) {
        return roleService.getOneRole(roleId);
    }

    @Override
    public DtoPermission createRole(DtoPermissionIU newRole) {
        return roleService.createRole(newRole);
    }

    @Override
    public DtoPermission updateRole(Long roleId, DtoPermissionIU newRole) {
        return roleService.updateRole(roleId, newRole);
    }

    @Override
    public void deleteRole(Long roleId) {
        roleService.deleteRole(roleId);
    }
} 
package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoPermission;
import com.personneltrackingsystem.dto.DtoPermissionIU;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Role Permission Controller", description = "Role Permission CRUD operations")
@RequestMapping("/api/role-permission")
public interface RolePermissionController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoPermission> getAllRolePermissions();

    @GetMapping("/{rolePermissionId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    DtoPermission getOneRolePermission(@PathVariable Long rolePermissionId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    DtoPermission createRolePermission(@RequestBody DtoPermissionIU newRolePermission);

    @PutMapping("/{rolePermissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    DtoPermission updateRolePermission(@PathVariable Long rolePermissionId, @RequestBody DtoPermissionIU newRolePermission);

    @DeleteMapping("/{rolePermissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteRolePermission(@PathVariable Long rolePermissionId);
} 
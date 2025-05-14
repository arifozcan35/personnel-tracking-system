package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoPermission;
import com.personneltrackingsystem.dto.DtoPermissionIU;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Role Controller", description = "Role CRUD operations")
@RequestMapping("/api/role")
public interface RoleController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoPermission> getAllRoles();

    @GetMapping("/{roleId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    DtoPermission getOneRole(@PathVariable Long roleId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    DtoPermission createRole(@RequestBody DtoPermissionIU newRole);

    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    DtoPermission updateRole(@PathVariable Long roleId, @RequestBody DtoPermissionIU newRole);

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteRole(@PathVariable Long roleId);
} 
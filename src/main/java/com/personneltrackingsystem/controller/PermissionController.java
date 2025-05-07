package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.entity.Permission;
import com.personneltrackingsystem.entity.Role;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Permission Controller", description = "Permission CRUD operations")
@RequestMapping("/api/permissions")
public interface PermissionController {


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Long id);

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Permission>> getAllPermissions();

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission);

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Permission> updatePermission(@PathVariable Long id, @RequestBody Permission permission);

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id);

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Permission>> getPermissionsByRole(@PathVariable Role role);

    @PostMapping("/role/{role}/permission/{permissionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> assignPermissionToRole(@PathVariable Role role, @PathVariable Long permissionId);

    @DeleteMapping("/role/{role}/permission/{permissionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> removePermissionFromRole(@PathVariable Role role, @PathVariable Long permissionId);
} 
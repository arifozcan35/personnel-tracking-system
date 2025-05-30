package com.personneltrackingsystem.controller.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.personneltrackingsystem.controller.PermissionController;
import com.personneltrackingsystem.entity.Permission;
import com.personneltrackingsystem.entity.Role;
import com.personneltrackingsystem.service.PermissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PermissionControllerImpl implements PermissionController {

    private final PermissionService permissionService;


    @Override
    public ResponseEntity<Permission> getPermissionById(Long id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    @Override
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @Override
    public ResponseEntity<Permission> createPermission(Permission permission) {
        return new ResponseEntity<>(permissionService.createPermission(permission), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Permission> updatePermission( Long id, Permission permission) {
        permission.setId(id);
        return ResponseEntity.ok(permissionService.updatePermission(permission));
    }

    @Override
    public ResponseEntity<Void> deletePermission(Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<Permission>> getPermissionsByRole(Role role) {
        return ResponseEntity.ok(permissionService.getPermissionsByRole(role));
    }

    @Override
    public ResponseEntity<Void> assignPermissionToRole(Role role, Long permissionId) {
        permissionService.assignPermissionToRole(role, permissionId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> removePermissionFromRole(Role role, Long permissionId) {
        permissionService.removePermissionFromRole(role, permissionId);
        return ResponseEntity.ok().build();
    }

}

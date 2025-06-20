package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.entity.Permission;
import com.personneltrackingsystem.entity.Role;
import com.personneltrackingsystem.entity.RolePermission;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.repository.PermissionRepository;
import com.personneltrackingsystem.repository.RolePermissionRepository;
import com.personneltrackingsystem.service.impl.PermissionServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceImplTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission permission;
    private Role role;
    private RolePermission rolePermission;

    @BeforeEach
    void setUp() {
        permission = Permission.builder()
                .id(1L)
                .name("test-permission")
                .resource("test-resource")
                .method("GET")
                .pathPattern("/api/test/**")
                .description("Test Permission")
                .build();

        role = Role.ROLE_ADMIN;

        rolePermission = RolePermission.builder()
                .id(1L)
                .role(role)
                .permission(permission)
                .build();
    }

    @Test
    void getPermissionById_ExistingId_ReturnsPermission() {
        // arrange
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        // act
        Permission result = permissionService.getPermissionById(1L);

        // assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test-permission", result.getName());
        verify(permissionRepository).findById(1L);
    }

    @Test
    void getPermissionById_NonExistingId_ThrowsException() {
        // arrange
        when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(BaseException.class, () -> permissionService.getPermissionById(999L));
        verify(permissionRepository).findById(999L);
    }

    @Test
    void getAllPermissions_ReturnsAllPermissions() {
        // arrange
        List<Permission> permissions = Arrays.asList(
                permission,
                Permission.builder().id(2L).name("another-permission").resource("resource").method("POST").pathPattern("/api/another/**").build()
        );
        when(permissionRepository.findAll()).thenReturn(permissions);

        // act
        List<Permission> result = permissionService.getAllPermissions();

        // assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(permissionRepository).findAll();
    }

    @Test
    void createPermission_ValidPermission_ReturnsCreatedPermission() {
        // arrange
        Permission newPermission = Permission.builder()
                .name("new-permission")
                .resource("new-resource")
                .method("POST")
                .pathPattern("/api/new/**")
                .build();
        
        when(permissionRepository.findByName("new-permission")).thenReturn(Optional.empty());
        when(permissionRepository.save(any(Permission.class))).thenReturn(
                Permission.builder()
                    .id(2L)
                    .name("new-permission")
                    .resource("new-resource")
                    .method("POST")
                    .pathPattern("/api/new/**")
                    .build()
        );

        // act
        Permission result = permissionService.createPermission(newPermission);

        // assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("new-permission", result.getName());
        verify(permissionRepository).findByName("new-permission");
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void createPermission_DuplicateName_ThrowsException() {
        // arrange
        Permission newPermission = Permission.builder()
                .name("test-permission")
                .resource("new-resource")
                .method("POST")
                .pathPattern("/api/new/**")
                .build();
        
        when(permissionRepository.findByName("test-permission")).thenReturn(Optional.of(permission));

        // act & assert
        assertThrows(ValidationException.class, () -> permissionService.createPermission(newPermission));
        verify(permissionRepository).findByName("test-permission");
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void createPermission_MissingRequiredFields_ThrowsException() {
        // arrange
        Permission invalidPermission = Permission.builder()
                .name("") // Boş isim
                .build();

        // act & assert
        assertThrows(ValidationException.class, () -> permissionService.createPermission(invalidPermission));
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void updatePermission_ValidPermission_ReturnsUpdatedPermission() {
        // arrange
        Permission updatedPermission = Permission.builder()
                .id(1L)
                .name("updated-permission")
                .resource("updated-resource")
                .method("PUT")
                .pathPattern("/api/updated/**")
                .description("Updated Description")
                .build();
        
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(permissionRepository.findByName("updated-permission")).thenReturn(Optional.empty());
        when(permissionRepository.save(any(Permission.class))).thenReturn(updatedPermission);

        // act
        Permission result = permissionService.updatePermission(updatedPermission);

        // assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("updated-permission", result.getName());
        assertEquals("updated-resource", result.getResource());
        verify(permissionRepository).findById(1L);
        verify(permissionRepository).findByName("updated-permission");
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void updatePermission_NonExistingId_ThrowsException() {
        // arrange
        Permission nonExistingPermission = Permission.builder()
                .id(999L)
                .name("non-existing")
                .resource("resource")
                .method("GET")
                .pathPattern("/api/non-existing/**")
                .build();
        
        when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(BaseException.class, () -> permissionService.updatePermission(nonExistingPermission));
        verify(permissionRepository).findById(999L);
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void deletePermission_ExistingId_DeletesPermission() {
        // arrange
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        doNothing().when(permissionRepository).deleteById(1L);

        // act
        permissionService.deletePermission(1L);

        // assert
        verify(permissionRepository).findById(1L);
        verify(permissionRepository).deleteById(1L);
    }

    @Test
    void deletePermission_NonExistingId_ThrowsException() {
        // arrange
        when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(BaseException.class, () -> permissionService.deletePermission(999L));
        verify(permissionRepository).findById(999L);
        verify(permissionRepository, never()).deleteById(anyLong());
    }

    @Test
    void hasPermission_MatchingPermission_ReturnsTrue() {
        // arrange
        List<RolePermission> rolePermissions = Collections.singletonList(rolePermission);
        when(rolePermissionRepository.findByRole(role)).thenReturn(rolePermissions);

        // act
        boolean result = permissionService.hasPermission(role, "test-resource", "GET", "/api/test/123");

        // assert
        assertTrue(result);
        verify(rolePermissionRepository).findByRole(role);
    }

    @Test
    void hasPermission_NonMatchingPermission_ReturnsFalse() {
        // arrange
        List<RolePermission> rolePermissions = Collections.singletonList(rolePermission);
        when(rolePermissionRepository.findByRole(role)).thenReturn(rolePermissions);

        // act
        boolean result = permissionService.hasPermission(role, "different-resource", "POST", "/api/different/123");

        // assert
        assertFalse(result);
        verify(rolePermissionRepository).findByRole(role);
    }

    @Test
    void getPermissionsByRole_ReturnsRolePermissions() {
        // arrange
        List<RolePermission> rolePermissions = Collections.singletonList(rolePermission);
        when(rolePermissionRepository.findByRole(role)).thenReturn(rolePermissions);

        // act
        List<Permission> result = permissionService.getPermissionsByRole(role);

        // assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(rolePermissionRepository).findByRole(role);
    }

    @Test
    void assignPermissionToRole_NewAssignment_Succeeds() {
        // arrange
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(rolePermissionRepository.findByRole(role)).thenReturn(Collections.emptyList());
        when(rolePermissionRepository.save(any(RolePermission.class))).thenReturn(rolePermission);

        // act
        permissionService.assignPermissionToRole(role, 1L);

        // assert
        verify(permissionRepository).findById(1L);
        verify(rolePermissionRepository).findByRole(role);
        verify(rolePermissionRepository).save(any(RolePermission.class));
    }

    @Test
    void assignPermissionToRole_AlreadyAssigned_ThrowsException() {
        // arrange
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(rolePermissionRepository.findByRole(role)).thenReturn(Collections.singletonList(rolePermission));

        // act & assert
        assertThrows(ValidationException.class, () -> permissionService.assignPermissionToRole(role, 1L));
        verify(permissionRepository).findById(1L);
        verify(rolePermissionRepository).findByRole(role);
        verify(rolePermissionRepository, never()).save(any(RolePermission.class));
    }

    @Test
    void removePermissionFromRole_AssignedPermission_Removes() {
        // arrange
        List<RolePermission> rolePermissions = Collections.singletonList(rolePermission);
        when(rolePermissionRepository.findByRole(role)).thenReturn(rolePermissions);
        doNothing().when(rolePermissionRepository).delete(any(RolePermission.class));

        // act
        permissionService.removePermissionFromRole(role, 1L);

        // assert
        verify(rolePermissionRepository).findByRole(role);
        verify(rolePermissionRepository).delete(any(RolePermission.class));
    }

    @Test
    void removePermissionFromRole_UnassignedPermission_ThrowsException() {
        // arrange
        when(rolePermissionRepository.findByRole(role)).thenReturn(Collections.emptyList());

        // act & assert
        assertThrows(ValidationException.class, () -> permissionService.removePermissionFromRole(role, 1L));
        verify(rolePermissionRepository).findByRole(role);
        verify(rolePermissionRepository, never()).delete(any(RolePermission.class));
    }
} 
package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.entity.Permission;
import com.personneltrackingsystem.entity.Role;
import com.personneltrackingsystem.entity.RolePermission;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.repository.PermissionRepository;
import com.personneltrackingsystem.repository.RolePermissionRepository;
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
        // Test verilerini hazırla
        permission = Permission.builder()
                .id(1L)
                .name("test-permission")
                .resource("test-resource")
                .method("GET")
                .pathPattern("/api/test/**")
                .description("Test Permission")
                .build();

        // Role bir enum olduğu için builder kullanımı yerine direkt enum değeri atanır
        role = Role.ROLE_ADMIN;

        rolePermission = RolePermission.builder()
                .id(1L)
                .role(role)
                .permission(permission)
                .build();
    }

    @Test
    void getPermissionById_ExistingId_ReturnsPermission() {
        // Hazırlık
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        // İşlem
        Permission result = permissionService.getPermissionById(1L);

        // Doğrulama
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test-permission", result.getName());
        verify(permissionRepository).findById(1L);
    }

    @Test
    void getPermissionById_NonExistingId_ThrowsException() {
        // Hazırlık
        when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

        // İşlem & Doğrulama
        assertThrows(BaseException.class, () -> permissionService.getPermissionById(999L));
        verify(permissionRepository).findById(999L);
    }

    @Test
    void getAllPermissions_ReturnsAllPermissions() {
        // Hazırlık
        List<Permission> permissions = Arrays.asList(
                permission,
                Permission.builder().id(2L).name("another-permission").resource("resource").method("POST").pathPattern("/api/another/**").build()
        );
        when(permissionRepository.findAll()).thenReturn(permissions);

        // İşlem
        List<Permission> result = permissionService.getAllPermissions();

        // Doğrulama
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(permissionRepository).findAll();
    }

    @Test
    void createPermission_ValidPermission_ReturnsCreatedPermission() {
        // Hazırlık
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

        // İşlem
        Permission result = permissionService.createPermission(newPermission);

        // Doğrulama
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("new-permission", result.getName());
        verify(permissionRepository).findByName("new-permission");
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void createPermission_DuplicateName_ThrowsException() {
        // Hazırlık
        Permission newPermission = Permission.builder()
                .name("test-permission")
                .resource("new-resource")
                .method("POST")
                .pathPattern("/api/new/**")
                .build();
        
        when(permissionRepository.findByName("test-permission")).thenReturn(Optional.of(permission));

        // İşlem & Doğrulama
        assertThrows(ValidationException.class, () -> permissionService.createPermission(newPermission));
        verify(permissionRepository).findByName("test-permission");
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void createPermission_MissingRequiredFields_ThrowsException() {
        // Hazırlık
        Permission invalidPermission = Permission.builder()
                .name("") // Boş isim
                .build();

        // İşlem & Doğrulama
        assertThrows(ValidationException.class, () -> permissionService.createPermission(invalidPermission));
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void updatePermission_ValidPermission_ReturnsUpdatedPermission() {
        // Hazırlık
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

        // İşlem
        Permission result = permissionService.updatePermission(updatedPermission);

        // Doğrulama
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
        // Hazırlık
        Permission nonExistingPermission = Permission.builder()
                .id(999L)
                .name("non-existing")
                .resource("resource")
                .method("GET")
                .pathPattern("/api/non-existing/**")
                .build();
        
        when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

        // İşlem & Doğrulama
        assertThrows(BaseException.class, () -> permissionService.updatePermission(nonExistingPermission));
        verify(permissionRepository).findById(999L);
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void deletePermission_ExistingId_DeletesPermission() {
        // Hazırlık
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        doNothing().when(permissionRepository).deleteById(1L);

        // İşlem
        permissionService.deletePermission(1L);

        // Doğrulama
        verify(permissionRepository).findById(1L);
        verify(permissionRepository).deleteById(1L);
    }

    @Test
    void deletePermission_NonExistingId_ThrowsException() {
        // Hazırlık
        when(permissionRepository.findById(999L)).thenReturn(Optional.empty());

        // İşlem & Doğrulama
        assertThrows(BaseException.class, () -> permissionService.deletePermission(999L));
        verify(permissionRepository).findById(999L);
        verify(permissionRepository, never()).deleteById(anyLong());
    }

    @Test
    void hasPermission_MatchingPermission_ReturnsTrue() {
        // Hazırlık
        List<RolePermission> rolePermissions = Collections.singletonList(rolePermission);
        when(rolePermissionRepository.findByRole(role)).thenReturn(rolePermissions);

        // İşlem
        boolean result = permissionService.hasPermission(role, "test-resource", "GET", "/api/test/123");

        // Doğrulama
        assertTrue(result);
        verify(rolePermissionRepository).findByRole(role);
    }

    @Test
    void hasPermission_NonMatchingPermission_ReturnsFalse() {
        // Hazırlık
        List<RolePermission> rolePermissions = Collections.singletonList(rolePermission);
        when(rolePermissionRepository.findByRole(role)).thenReturn(rolePermissions);

        // İşlem
        boolean result = permissionService.hasPermission(role, "different-resource", "POST", "/api/different/123");

        // Doğrulama
        assertFalse(result);
        verify(rolePermissionRepository).findByRole(role);
    }

    @Test
    void getPermissionsByRole_ReturnsRolePermissions() {
        // Hazırlık
        List<RolePermission> rolePermissions = Collections.singletonList(rolePermission);
        when(rolePermissionRepository.findByRole(role)).thenReturn(rolePermissions);

        // İşlem
        List<Permission> result = permissionService.getPermissionsByRole(role);

        // Doğrulama
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(rolePermissionRepository).findByRole(role);
    }

    @Test
    void assignPermissionToRole_NewAssignment_Succeeds() {
        // Hazırlık
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(rolePermissionRepository.findByRole(role)).thenReturn(Collections.emptyList());
        when(rolePermissionRepository.save(any(RolePermission.class))).thenReturn(rolePermission);

        // İşlem
        permissionService.assignPermissionToRole(role, 1L);

        // Doğrulama
        verify(permissionRepository).findById(1L);
        verify(rolePermissionRepository).findByRole(role);
        verify(rolePermissionRepository).save(any(RolePermission.class));
    }

    @Test
    void assignPermissionToRole_AlreadyAssigned_ThrowsException() {
        // Hazırlık
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(rolePermissionRepository.findByRole(role)).thenReturn(Collections.singletonList(rolePermission));

        // İşlem & Doğrulama
        assertThrows(ValidationException.class, () -> permissionService.assignPermissionToRole(role, 1L));
        verify(permissionRepository).findById(1L);
        verify(rolePermissionRepository).findByRole(role);
        verify(rolePermissionRepository, never()).save(any(RolePermission.class));
    }

    @Test
    void removePermissionFromRole_AssignedPermission_Removes() {
        // Hazırlık
        List<RolePermission> rolePermissions = Collections.singletonList(rolePermission);
        when(rolePermissionRepository.findByRole(role)).thenReturn(rolePermissions);
        doNothing().when(rolePermissionRepository).delete(any(RolePermission.class));

        // İşlem
        permissionService.removePermissionFromRole(role, 1L);

        // Doğrulama
        verify(rolePermissionRepository).findByRole(role);
        verify(rolePermissionRepository).delete(any(RolePermission.class));
    }

    @Test
    void removePermissionFromRole_UnassignedPermission_ThrowsException() {
        // Hazırlık
        when(rolePermissionRepository.findByRole(role)).thenReturn(Collections.emptyList());

        // İşlem & Doğrulama
        assertThrows(ValidationException.class, () -> permissionService.removePermissionFromRole(role, 1L));
        verify(rolePermissionRepository).findByRole(role);
        verify(rolePermissionRepository, never()).delete(any(RolePermission.class));
    }
} 
package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Role;
import com.personneltrackingsystem.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    @Modifying
    @Query("UPDATE User u SET u.rolePermission = NULL WHERE u.rolePermission = :rolePermission")
    void updateUserRolePermissionReferences(@Param("rolePermission") RolePermission rolePermission);

    boolean existsByRolePermissionName(String existingRolePermissionName);

    boolean existsById(Long existingRolePermissionID);

    List<RolePermission> findByRole(Role role);


} 
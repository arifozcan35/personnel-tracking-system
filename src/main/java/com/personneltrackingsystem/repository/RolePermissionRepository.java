package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Role;
import com.personneltrackingsystem.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRole(Role role);
} 
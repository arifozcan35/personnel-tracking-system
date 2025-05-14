package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Modifying
    @Query("UPDATE User u SET u.permission = NULL WHERE u.permission = :permission")
    void updateUserPermissionReferences(@Param("permission") Permission permission);

    boolean existsByName(String existingPermissionName);

    boolean existsById(Long existingPermissionID);

    Optional<Permission> findByName(String name);
    Optional<Permission> findByResourceAndMethodAndPathPattern(String resource, String method, String pathPattern);
} 
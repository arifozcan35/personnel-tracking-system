package com.personneltrackingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role_permission", schema = "dbpersonel")
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rolePermissionSeq")
    @SequenceGenerator(name = "rolePermissionSeq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;
} 
package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.Permission;
import com.personneltrackingsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoRolePermissionIU {
    private Long id;
    private Role role;
    private Permission permission;
} 
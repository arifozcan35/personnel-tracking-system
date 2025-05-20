package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.Permission;
import com.personneltrackingsystem.entity.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoRolePermission {

    @Schema(description = "The role of the user", example = "ADMIN")
    private Role role;

    @Schema(description = "The permission of the user", example = "READ")
    private Permission permission;
} 
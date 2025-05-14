package com.personneltrackingsystem.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.personneltrackingsystem.dto.DtoPermission;
import com.personneltrackingsystem.dto.DtoPermissionIU;
import com.personneltrackingsystem.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission dtoPermissionToPermission(DtoPermission dtoPermission);

    DtoPermission permissionToDtoPermission(Permission permission); 

    List<DtoPermission> permissionsToDtoPermissions(List<Permission> permissions);

    List<Permission> dtoPermissionsToPermissions(List<DtoPermission> dtoPermissions);

    DtoPermissionIU permissionToDtoPermissionIU(Permission permission);

    Permission dtoPermissionIUToPermission(DtoPermissionIU permissionIU);

}

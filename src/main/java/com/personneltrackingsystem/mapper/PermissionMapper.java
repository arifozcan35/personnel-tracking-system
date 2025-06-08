package com.personneltrackingsystem.mapper;

import org.mapstruct.Mapper;

import com.personneltrackingsystem.dto.DtoPermission;

import com.personneltrackingsystem.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission dtoPermissionToPermission(DtoPermission dtoPermission);

    DtoPermission permissionToDtoPermission(Permission permission); 

}

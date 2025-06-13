package com.personneltrackingsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.personneltrackingsystem.dto.DtoPermission;

import com.personneltrackingsystem.entity.Permission;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {

    Permission dtoPermissionToPermission(DtoPermission dtoPermission);

    DtoPermission permissionToDtoPermission(Permission permission); 

}

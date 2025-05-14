package com.personneltrackingsystem.mapper;

import java.util.List;

import com.personneltrackingsystem.dto.DtoRolePermission;
import com.personneltrackingsystem.dto.DtoRolePermissionIU;
import com.personneltrackingsystem.entity.RolePermission;

import org.mapstruct.Mapper;    

@Mapper(componentModel = "spring")
public interface RolePermissionMapper {

    RolePermission dtoRolePermissionIUToRolePermission(DtoRolePermissionIU dtoRolePermissionIU);

    DtoRolePermission rolePermissionToDtoRolePermission(RolePermission rolePermission);

    List<DtoRolePermission> rolePermissionListToDtoRolePermissionList(List<RolePermission> rolePermissionList);

    List<RolePermission> dtoRolePermissionListToRolePermissionList(List<DtoRolePermission> dtoRolePermissionList);

    DtoRolePermissionIU rolePermissionToDtoRolePermissionIU(RolePermission rolePermission);
}

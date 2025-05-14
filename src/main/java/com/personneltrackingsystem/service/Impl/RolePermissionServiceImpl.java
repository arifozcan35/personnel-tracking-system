package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoRolePermission;
import com.personneltrackingsystem.dto.DtoRolePermissionIU;
import com.personneltrackingsystem.entity.RolePermission;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.RolePermissionMapper;
import com.personneltrackingsystem.repository.RolePermissionRepository;
import com.personneltrackingsystem.service.RolePermissionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    private final RolePermissionMapper rolePermissionMapper;

    private final MessageResolver messageResolver;

    @Override
    public List<DtoRolePermission> getAllRolePermissions(){

        List<RolePermission> rolePermissionList =  rolePermissionRepository.findAll();

        return rolePermissionMapper.rolePermissionListToDtoRolePermissionList(rolePermissionList);
    }

    @Override
    public Optional<DtoRolePermission> getRolePermissionById(Long rolePermissionId) {

        RolePermission rolePermission = rolePermissionRepository.findById(rolePermissionId)
                .orElseThrow(() -> new EntityNotFoundException("Role permission not found with id: " + rolePermissionId));

        return Optional.ofNullable(rolePermissionMapper.rolePermissionToDtoRolePermission(rolePermission));
    }

    @Override
    public DtoRolePermission getOneRolePermission(Long rolePermissionId){
        Optional<RolePermission> optRolePermission =  rolePermissionRepository.findById(rolePermissionId);
        if(optRolePermission.isEmpty()){
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }else{
            return rolePermissionMapper.rolePermissionToDtoRolePermission(optRolePermission.get());
        }
    }

    @Override
    @Transactional
    public DtoRolePermission saveOneRolePermission(DtoRolePermissionIU rolePermission) {

        if (!ObjectUtils.isEmpty(rolePermission.getRolePermissionId())) {
            if (rolePermissionRepository.existsById(rolePermission.getRolePermissionId())) {
                throw new ValidationException("Role permission with this role permission ID already exists!");
            }
        }

        String rolePermissionName = rolePermission.getRolePermissionName();
        if (ObjectUtils.isEmpty(rolePermissionName)) {
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }

        if (rolePermissionRepository.existsByRolePermissionName(rolePermissionName)) {
            throw new ValidationException("Role permission with this role permission name already exists!");
        }

        RolePermission pRolePermission = rolePermissionMapper.dtoRolePermissionIUToRolePermission(rolePermission);
        RolePermission dbRolePermission = rolePermissionRepository.save(pRolePermission);

        return rolePermissionMapper.rolePermissionToDtoRolePermission(dbRolePermission);

    }

    @Override
    @Transactional
    public DtoRolePermission updateOneRolePermission(Long id, DtoRolePermissionIU newRolePermission) {

        Optional<RolePermission> optRolePermission = rolePermissionRepository.findById(id);

        if(optRolePermission.isPresent()){
            RolePermission foundRolePermission = optRolePermission.get();
            foundRolePermission.setRolePermissionName(newRolePermission.getRolePermissionName());

            RolePermission updatedRolePermission = rolePermissionRepository.save(foundRolePermission);

            return rolePermissionMapper.rolePermissionToDtoRolePermission(updatedRolePermission);
        }else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }

    }

    @Override
    @Transactional
    public void deleteOneRolePermission(Long rolePermissionId) {
        Optional<RolePermission> optRolePermission = rolePermissionRepository.findById(rolePermissionId);

        if(optRolePermission.isPresent()){
            // update associated personnel records
            rolePermissionRepository.updateUserRolePermissionReferences(optRolePermission.get());

            // delete role permission
            rolePermissionRepository.delete(optRolePermission.get());
        }
        else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }
} 
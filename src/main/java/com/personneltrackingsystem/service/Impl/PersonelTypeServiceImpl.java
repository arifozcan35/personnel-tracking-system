package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoPersonelType;
import com.personneltrackingsystem.entity.PersonelType;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.PersonelTypeMapper;
import com.personneltrackingsystem.repository.PersonelTypeRepository;
import com.personneltrackingsystem.service.PersonelTypeService;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonelTypeServiceImpl implements PersonelTypeService {

    private final PersonelTypeRepository personelTypeRepository;

    private final PersonelTypeMapper personelTypeMapper;

    @Override
    public List<DtoPersonelType> getAllPersonelTypes(){

        List<PersonelType> personelTypeList =  personelTypeRepository.findAll();

        return personelTypeMapper.personelTypeListToDtoPersonelTypeList(personelTypeList);
    }

    @Override
    public Optional<DtoPersonelType> getPersonelTypeById(Long personelTypeId) {

        PersonelType personelType = personelTypeRepository.findById(personelTypeId)
                .orElseThrow(() -> new BaseException(MessageType.PERSONNEL_TYPE_NOT_FOUND, personelTypeId.toString()));

        return Optional.ofNullable(personelTypeMapper.personelTypeToDtoPersonelType(personelType));
    }

    @Override
    public DtoPersonelType getOnePersonelType(Long personelTypeId){
        Optional<PersonelType> optPersonelType =  personelTypeRepository.findById(personelTypeId);
        if(optPersonelType.isEmpty()){
            throw new BaseException(MessageType.PERSONNEL_TYPE_NOT_FOUND, personelTypeId.toString());
        }else{
            return personelTypeMapper.personelTypeToDtoPersonelType(optPersonelType.get());
        }
    }

    @Override
    @Transactional
    public DtoPersonelType saveOnePersonelType(DtoPersonelType personelType) {

        String personelTypeName = personelType.getPersonelTypeName();
        if (ObjectUtils.isEmpty(personelTypeName)) {
            throw new ValidationException(MessageType.REQUIRED_FIELD_AVAILABLE);
        }

        if (personelTypeRepository.existsByPersonelTypeName(personelTypeName)) {
            throw new ValidationException(MessageType.PERSONNEL_TYPE_NAME_ALREADY_EXISTS, personelTypeName);
        }

        PersonelType pPersonelType = personelTypeMapper.dtoPersonelTypeToPersonelType(personelType);
        PersonelType dbPersonelType = personelTypeRepository.save(pPersonelType);

        return personelTypeMapper.personelTypeToDtoPersonelType(dbPersonelType);

    }

    @Override
    @Transactional
    public DtoPersonelType updateOnePersonelType(Long id, DtoPersonelType newPersonelType) {
        PersonelType existingPersonelType = personelTypeRepository.findById(id)
                .orElseThrow(() -> new BaseException(MessageType.PERSONNEL_TYPE_NOT_FOUND, id.toString()));

        if (ObjectUtils.isNotEmpty(newPersonelType.getPersonelTypeName())) {
            // Check uniqueness if the name is being changed
            if (!existingPersonelType.getPersonelTypeName().equals(newPersonelType.getPersonelTypeName()) && 
                personelTypeRepository.existsByPersonelTypeName(newPersonelType.getPersonelTypeName())) {
                throw new ValidationException(MessageType.PERSONNEL_TYPE_NAME_ALREADY_EXISTS, newPersonelType.getPersonelTypeName());
            }
            existingPersonelType.setPersonelTypeName(newPersonelType.getPersonelTypeName());
        }

        PersonelType updatedPersonelType = personelTypeRepository.save(existingPersonelType);
        return personelTypeMapper.personelTypeToDtoPersonelType(updatedPersonelType);
    }

    @Override
    @Transactional
    public void deleteOnePersonelType(Long personelTypeId) {
        Optional<PersonelType> optPersonelType = personelTypeRepository.findById(personelTypeId);

        if(optPersonelType.isPresent()){
            personelTypeRepository.delete(optPersonelType.get());
        }
        else{
            throw new BaseException(MessageType.PERSONNEL_TYPE_NOT_FOUND, personelTypeId.toString());
        }
    }
} 
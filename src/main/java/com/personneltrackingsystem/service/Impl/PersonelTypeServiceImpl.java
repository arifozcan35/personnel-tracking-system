package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoPersonelType;
import com.personneltrackingsystem.dto.DtoPersonelTypeIU;
import com.personneltrackingsystem.entity.PersonelType;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.PersonelTypeMapper;
import com.personneltrackingsystem.repository.PersonelTypeRepository;
import com.personneltrackingsystem.service.PersonelTypeService;

import jakarta.persistence.EntityNotFoundException;
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

    private final MessageResolver messageResolver;

    @Override
    public List<DtoPersonelType> getAllPersonelTypes(){

        List<PersonelType> personelTypeList =  personelTypeRepository.findAll();

        return personelTypeMapper.personelTypeListToDtoPersonelTypeList(personelTypeList);
    }

    @Override
    public Optional<DtoPersonelType> getPersonelTypeById(Long personelTypeId) {

        PersonelType personelType = personelTypeRepository.findById(personelTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Personel type not found with id: " + personelTypeId));

        return Optional.ofNullable(personelTypeMapper.personelTypeToDtoPersonelType(personelType));
    }

    @Override
    public DtoPersonelType getOnePersonelType(Long personelTypeId){
        Optional<PersonelType> optPersonelType =  personelTypeRepository.findById(personelTypeId);
        if(optPersonelType.isEmpty()){
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }else{
            return personelTypeMapper.personelTypeToDtoPersonelType(optPersonelType.get());
        }
    }

    @Override
    @Transactional
    public DtoPersonelType saveOnePersonelType(DtoPersonelTypeIU personelType) {

        if (!ObjectUtils.isEmpty(personelType.getPersonelTypeId())) {
            if (personelTypeRepository.existsById(personelType.getPersonelTypeId())) {
                throw new ValidationException("Personel type with this personel type ID already exists!");
            }
        }

        String personelTypeName = personelType.getPersonelTypeName();
        if (ObjectUtils.isEmpty(personelTypeName)) {
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }

        if (personelTypeRepository.existsByPersonelTypeName(personelTypeName)) {
            throw new ValidationException("Personel type with this personel type name already exists!");
        }

        PersonelType pPersonelType = personelTypeMapper.dtoPersonelTypeIUToPersonelType(personelType);
        PersonelType dbPersonelType = personelTypeRepository.save(pPersonelType);

        return personelTypeMapper.personelTypeToDtoPersonelType(dbPersonelType);

    }

    @Override
    @Transactional
    public DtoPersonelType updateOnePersonelType(Long id, DtoPersonelTypeIU newPersonelType) {

        Optional<PersonelType> optPersonelType = personelTypeRepository.findById(id);

        if(optPersonelType.isPresent()){
            PersonelType foundPersonelType = optPersonelType.get();
            foundPersonelType.setPersonelTypeName(newPersonelType.getPersonelTypeName());

            PersonelType updatedPersonelType = personelTypeRepository.save(foundPersonelType);

            return personelTypeMapper.personelTypeToDtoPersonelType(updatedPersonelType);
        }else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }

    }

    @Override
    @Transactional
    public void deleteOnePersonelType(Long personelTypeId) {
        Optional<PersonelType> optPersonelType = personelTypeRepository.findById(personelTypeId);

        if(optPersonelType.isPresent()){
            personelTypeRepository.delete(optPersonelType.get());
        }
        else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }
} 
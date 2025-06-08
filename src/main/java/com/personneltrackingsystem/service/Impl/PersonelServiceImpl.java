package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.*;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.exception.*;
import com.personneltrackingsystem.mapper.PersonelMapper;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.PersonelService;
import com.personneltrackingsystem.service.PersonelCacheService;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class PersonelServiceImpl implements PersonelService  {

    private final PersonelRepository personelRepository;

    private final PersonelMapper personelMapper;

    private final PersonelCacheService personelCacheService;


    @Override
    public Personel checkIfPersonelExists(Long personelId){
        return personelRepository.findById(personelId)
            .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PERSONNEL_NOT_FOUND, personelId.toString())));
    }


    @Override
    public List<DtoPersonel> getAllPersonels() {
        List<Personel> personelList = personelRepository.findAll();
        return personelMapper.personelsToDtoPersonels(personelList);
    }


    @Override
    public DtoPersonelAll getAOnePersonel(Long personelId) {
        Optional<Personel> optPersonel = personelRepository.findById(personelId);

        if(optPersonel.isPresent()) {
            Personel personel = optPersonel.get();
            
            if (ObjectUtils.isNotEmpty(personel.getPersonelTypeId())) {
                personel.getPersonelTypeId().getPersonelTypeName();
            }
            
            if (ObjectUtils.isNotEmpty(personel.getUnitId()) && !personel.getUnitId().isEmpty()) {
                personel.getUnitId().forEach(unit -> unit.getUnitName());
            }
            
            return personelMapper.personelToDtoPersonelAll(personel);
        } else {
            throw new BaseException(new ErrorMessage(MessageType.PERSONNEL_NOT_FOUND, personelId.toString()));
        }
    }



    @Override
    @Transactional
    public ResponseEntity<String> saveOnePersonel(DtoPersonelIU newPersonel) {
        // basic data validation
        if (ObjectUtils.isEmpty(newPersonel.getName())) {
            throw new ValidationException(MessageType.PERSONNEL_NAME_REQUIRED);
        }
        
        if (ObjectUtils.isEmpty(newPersonel.getEmail())) {
            throw new ValidationException(MessageType.PERSONNEL_EMAIL_REQUIRED);
        }

        DtoPersonelIU personelToPut = newPersonel;

        // check email uniqueness
        Optional<Personel> existingPersonnel = personelRepository.findByEmail(newPersonel.getEmail());
        if (existingPersonnel.isPresent()) {
            throw new ValidationException(MessageType.PERSONNEL_EMAIL_ALREADY_EXISTS, newPersonel.getEmail());
        }

        // handle personel type if provided
        if (!ObjectUtils.isEmpty(newPersonel.getPersonelTypeId())) {
            personelToPut.setPersonelTypeId(newPersonel.getPersonelTypeId());
        }

        // handle units if provided
        if (!ObjectUtils.isEmpty(newPersonel.getUnitId())) {
            personelToPut.setUnitId(newPersonel.getUnitId());
        }

        Personel personelToSave = personelMapper.dtoPersonelIUToPersonel(personelToPut);


        try {
            Personel savedPersonnel = personelRepository.save(personelToSave);
            return new ResponseEntity<>("Personnel registered successfully with ID: " + savedPersonnel.getPersonelId(), HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Could not save personnel due to a data integrity violation.", HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    @Transactional
    public ResponseEntity<String> updateOnePersonel(Long id, DtoPersonelIU newPersonel) {
        Optional<Personel> optPersonel = personelRepository.findById(id);

        if (optPersonel.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.PERSONNEL_NOT_FOUND, id.toString()));
        }

        Personel foundPersonel = optPersonel.get();

        // update name
        if (!ObjectUtils.isEmpty(newPersonel.getName())) {
            foundPersonel.setName(newPersonel.getName());
        }

        // update email with uniqueness check
        if (!ObjectUtils.isEmpty(newPersonel.getEmail())) {
            Optional<Personel> existingEmail = personelRepository.findByEmail(newPersonel.getEmail());
            if (existingEmail.isPresent() && !existingEmail.get().getPersonelId().equals(id)) {
                throw new ValidationException(MessageType.PERSONNEL_EMAIL_ALREADY_EXISTS, newPersonel.getEmail());
            }
            foundPersonel.setEmail(newPersonel.getEmail());
        }

        // update personnel type
        if (!ObjectUtils.isEmpty(newPersonel.getPersonelTypeId())) {
            foundPersonel.setPersonelTypeId(personelMapper.longToPersonelTypeEntity(newPersonel.getPersonelTypeId()));
        }

        // update units
        if (!ObjectUtils.isEmpty(newPersonel.getUnitId())) {
            foundPersonel.setUnitId(personelMapper.longListToUnitEntityList(newPersonel.getUnitId()));
        }

        try {
            Personel updatedPersonnel = personelRepository.save(foundPersonel);
            
            // invalidate cache after update
            personelCacheService.removePersonelFromCache(id);
            
            return new ResponseEntity<>("Personnel updated successfully with ID: " + updatedPersonnel.getPersonelId(), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Could not update personnel due to a data integrity violation.", HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    @Transactional
    public void deleteOnePersonel(Long id) {
        Optional<Personel> optPersonel = personelRepository.findById(id);
        if (optPersonel.isPresent()) {
            personelRepository.deleteById(id);
            
            // invalidate cache after delete
            personelCacheService.removePersonelFromCache(id);
        } else {
            throw new BaseException(new ErrorMessage(MessageType.PERSONNEL_NOT_FOUND, id.toString()));
        }
    }


    @Override
    public Set<DtoPersonel> getPersonelsByUnitId(Long unitId) {
        Set<DtoPersonel> personels = new HashSet<>();
        
        // find all personel that belong to the specified unit
        List<Personel> personnelList = personelRepository.findAll();
        
        for (Personel personel : personnelList) {
            if (personel.getUnitId() != null) {
                for (Unit unit : personel.getUnitId()) {
                    if (unit.getUnitId().equals(unitId)) {
                        personels.add(personelMapper.personelToDtoPersonel(personel));
                        break;
                    }
                }
            }
        }
        
        if (personels.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "No personnel found for unit ID: " + unitId.toString()));
        }
        
        return personels;
    }

    
    @Override
    public Personel getPersonelWithCache(Long personelId) {
        // first try to get from cache
        Optional<Personel> cachedPersonel = personelCacheService.getPersonelFromCache(personelId);
        if (cachedPersonel.isPresent()) {
            return cachedPersonel.get();
        }

        // if not in cache, get from database with all relationships initialized using the custom repository method
        Personel personel = personelRepository.findByIdWithRelationships(personelId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PERSONNEL_NOT_FOUND, personelId.toString())));
        
        // cache the personel data (relationships are already loaded)
        personelCacheService.cachePersonel(personelId, personel);
        
        return personel;
    }

}

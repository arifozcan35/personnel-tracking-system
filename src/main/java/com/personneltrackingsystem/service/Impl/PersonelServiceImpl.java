package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.*;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.exception.*;
import com.personneltrackingsystem.mapper.PersonelMapper;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.PersonelService;
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


    @Override
    public List<DtoPersonel> getAllPersonels() {
        List<Personel> personelList = personelRepository.findAll();
        return personelMapper.personelsToDtoPersonels(personelList);
    }

    @Override
    public DtoPersonel getAOnePersonel(Long personelId) {
        Optional<Personel> optPersonel = personelRepository.findById(personelId);

        if(optPersonel.isPresent()) {
            return personelMapper.personelToDtoPersonel(optPersonel.get());
        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, personelId.toString()));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> saveOnePersonel(DtoPersonelIU newPersonel) {
        // Basic data validation
        if (ObjectUtils.isEmpty(newPersonel.getName()) || ObjectUtils.isEmpty(newPersonel.getEmail())) {
            throw new ValidationException("Personnel name and email are required!");
        }

        DtoPersonelIU personelToPut = newPersonel;

        // Check email uniqueness
        Optional<Personel> existingPersonnel = personelRepository.findByEmail(newPersonel.getEmail());
        if (existingPersonnel.isPresent()) {
            throw new ValidationException("Personnel with this email already exists!");
        }

        // Handle personnel type if provided
        if (!ObjectUtils.isEmpty(newPersonel.getPersonelTypeId())) {
            personelToPut.setPersonelTypeId(newPersonel.getPersonelTypeId());
        }

        // Handle units if provided
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
            return new ResponseEntity<>("No personnel found!", HttpStatus.NOT_FOUND);
        }

        Personel foundPersonel = optPersonel.get();

        // Update name
        if (!ObjectUtils.isEmpty(newPersonel.getName())) {
            foundPersonel.setName(newPersonel.getName());
        }

        // Update email with uniqueness check
        if (!ObjectUtils.isEmpty(newPersonel.getEmail())) {
            Optional<Personel> existingEmail = personelRepository.findByEmail(newPersonel.getEmail());
            if (existingEmail.isPresent() && !existingEmail.get().getPersonelId().equals(id)) {
                throw new ValidationException("Email is already in use by another personnel!");
            }
            foundPersonel.setEmail(newPersonel.getEmail());
        }

        // Update personnel type
        if (!ObjectUtils.isEmpty(newPersonel.getPersonelTypeId())) {
            foundPersonel.setPersonelTypeId(personelMapper.map(newPersonel.getPersonelTypeId()));
        }

        // Update units
        if (!ObjectUtils.isEmpty(newPersonel.getUnitId())) {
            foundPersonel.setUnitId(personelMapper.map2(newPersonel.getUnitId()));
        }

        try {
            Personel updatedPersonnel = personelRepository.save(foundPersonel);
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
        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString()));
        }
    }

    @Override
    public Set<DtoPersonel> getPersonelsByUnitId(Long unitId) {
        Set<DtoPersonel> personels = new HashSet<>();
        
        // Find all personnel that belong to the specified unit
        List<Personel> personnelList = personelRepository.findAll();
        
        for (Personel personel : personnelList) {
            if (personel.getUnitId() != null) {
                for (DtoUnitIU unit : personel.getUnitId()) {
                    if (unit.getUnitId().equals(unitId)) {
                        personels.add(personelMapper.personelToDtoPersonel(personel));
                        break;
                    }
                }
            }
        }
        
        if (personels.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, unitId.toString()));
        }
        
        return personels;
    }

    /*
    @Override
    public Map<String, Double> listSalaries() {
        List<Personel> allPersonels = personelRepository.findAll();
        
        Map<String, Double> salaryMap = new HashMap<>();
        
        for (Personel personel : allPersonels) {
            salaryMap.put(personel.getName(), 0.0);
        }
        
        return salaryMap;
    }
    */
}

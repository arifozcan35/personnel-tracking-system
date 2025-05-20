package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Floor;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.UnitMapper;
import com.personneltrackingsystem.repository.FloorRepository;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.repository.UnitRepository;
import com.personneltrackingsystem.service.FloorService;
import com.personneltrackingsystem.service.PersonelService;
import com.personneltrackingsystem.service.UnitService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    private final FloorService floorService;

    private final PersonelService personelService;

    private final UnitMapper unitMapper;

    // Solid example : article 1 (Single Responsibility Principle)


    @Override
    public Optional<DtoUnit> findById(Long unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found with id: " + unitId));
        return Optional.ofNullable(unitMapper.unitToDtoUnit(unit));
    }

    @Override
    public Unit checkIfUnitExists(Long unitId) {
        return unitRepository.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found with id: " + unitId));
    }


    @Override
    public List<DtoUnit> getAllUnits(){

        List<Unit> unitList = unitRepository.findAll();

        return unitMapper.unitListToDtoUnitList(unitList);
    }


    @Override
    public DtoUnit getOneUnit(Long unitId){

        Optional<Unit> optUnit =  unitRepository.findById(unitId);
        if(optUnit.isPresent()){
            return unitMapper.unitToDtoUnit(optUnit.get());
        }
        else{
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, unitId.toString()));
        }

    }


    @Override
    @Transactional
    public DtoUnit saveOneUnit(DtoUnitIU unit) {

        String unitName = unit.getBirimIsim();
        if (ObjectUtils.isEmpty(unitName)) {
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }

        if (unitRepository.existsByUnitName(unitName)) {
            throw new ValidationException("Unit with this unit name already exists!");
        }

        // Find and set floor if floorId is provided
        if (ObjectUtils.isNotEmpty(unit.getFloorId())) {
            Floor floor = floorService.checkIfFloorExists(unit.getFloorId());
            unit.setFloorId(floor.getFloorId());
        }

        Unit pUnit = unitMapper.dtoUnitIUToUnit(unit);
        
        // Handle administratorPersonelId separately
        if (ObjectUtils.isNotEmpty(unit.getAdministratorPersonelId())) {
            Personel personel = personelService.checkIfPersonelExists(unit.getAdministratorPersonelId());
            pUnit.setAdministratorPersonelId(personel);
        } else {
            // Explicitly set to null to ensure it's not required
            pUnit.setAdministratorPersonelId(null);
        }

        Unit dbUnit = unitRepository.save(pUnit);

        return unitMapper.unitToDtoUnit(dbUnit);
    }


    @Override
    @Transactional
    public DtoUnit updateOneUnit(Long id, DtoUnitIU newUnit) {

        Optional<Unit> optUnit = unitRepository.findById(id);

        if(optUnit.isPresent()){
            DtoUnitIU foundUnit = unitMapper.unitToDtoUnitIU(optUnit.get());
            foundUnit.setBirimIsim(newUnit.getBirimIsim());

            if (ObjectUtils.isNotEmpty(newUnit.getFloorId())) {
                Floor floor = floorService.checkIfFloorExists(newUnit.getFloorId());
                foundUnit.setFloorId(floor.getFloorId());
            }

            if (ObjectUtils.isNotEmpty(newUnit.getAdministratorPersonelId())) {
                Personel personel = personelService.checkIfPersonelExists(newUnit.getAdministratorPersonelId());
                foundUnit.setAdministratorPersonelId(personel.getPersonelId());
            }

            Unit updatedUnit = unitMapper.dtoUnitIUToUnit(foundUnit);
            updatedUnit = unitRepository.save(updatedUnit);

            return unitMapper.unitToDtoUnit(updatedUnit);


        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString()));
        }
    }


    @Override
    @Transactional
    public void deleteOneUnit(Long unitId) {
        Optional<Unit> optUnit = unitRepository.findById(unitId);

        if (optUnit.isPresent()) {
            unitRepository.delete(optUnit.get());
        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, unitId.toString()));
        }
    }

}

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
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.UNIT_NOT_FOUND, unitId.toString())));
        return Optional.ofNullable(unitMapper.unitToDtoUnit(unit));
    }

    @Override
    public Unit checkIfUnitExists(Long unitId) {
        return unitRepository.findById(unitId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.UNIT_NOT_FOUND, unitId.toString())));
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
            throw new BaseException(new ErrorMessage(MessageType.UNIT_NOT_FOUND, unitId.toString()));
        }

    }


    @Override
    @Transactional
    public DtoUnit saveOneUnit(DtoUnitIU unit) {

        String unitName = unit.getBirimIsim();
        if (ObjectUtils.isEmpty(unitName)) {
            throw new ValidationException(MessageType.UNIT_NAME_REQUIRED);
        }

        if (unitRepository.existsByUnitName(unitName)) {
            throw new ValidationException(MessageType.UNIT_NAME_ALREADY_EXISTS, unitName);
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
        Unit existingUnit = unitRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.UNIT_NOT_FOUND, id.toString())));

        if (ObjectUtils.isNotEmpty(newUnit.getBirimIsim())) {
            // Check uniqueness if the name is being changed
            if (!existingUnit.getUnitName().equals(newUnit.getBirimIsim()) && 
                unitRepository.existsByUnitName(newUnit.getBirimIsim())) {
                throw new ValidationException(MessageType.UNIT_NAME_ALREADY_EXISTS, newUnit.getBirimIsim());
            }
            existingUnit.setUnitName(newUnit.getBirimIsim());
        }

        if (ObjectUtils.isNotEmpty(newUnit.getFloorId())) {
            Floor floor = floorService.checkIfFloorExists(newUnit.getFloorId());
            existingUnit.setFloorId(floor);
        }

        if (ObjectUtils.isNotEmpty(newUnit.getAdministratorPersonelId())) {
            Personel personel = personelService.checkIfPersonelExists(newUnit.getAdministratorPersonelId());
            existingUnit.setAdministratorPersonelId(personel);
        }

        Unit updatedUnit = unitRepository.save(existingUnit);

        return unitMapper.unitToDtoUnit(updatedUnit);
    }


    @Override
    @Transactional
    public void deleteOneUnit(Long unitId) {
        Optional<Unit> optUnit = unitRepository.findById(unitId);

        if (optUnit.isPresent()) {
            unitRepository.delete(optUnit.get());
        } else {
            throw new BaseException(new ErrorMessage(MessageType.UNIT_NOT_FOUND, unitId.toString()));
        }
    }

}

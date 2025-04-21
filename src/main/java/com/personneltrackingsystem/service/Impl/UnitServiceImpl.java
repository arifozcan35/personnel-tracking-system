package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.mapper.UnitMapper;
import com.personneltrackingsystem.repository.UnitRepository;
import com.personneltrackingsystem.service.UnitService;
import com.personneltrackingsystem.validator.UnitValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    private final UnitMapper unitMapper;

    private final UnitValidator unitValidator;

    // Solid example : article 1 (Single Responsibility Principle)

    @Override
    public Optional<DtoUnitIU> findById(Long unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found with id: " + unitId));
        return Optional.ofNullable(unitMapper.unitToDtoUnitIU(unit));
    }

    @Override
    public List<DtoUnit> getAllUnits(){
        List<Unit> unitList = unitRepository.findAll();
        List<DtoUnit> newUnitList = new ArrayList<>();

        for(Unit allUnits : unitList){
            newUnitList.add(unitMapper.unitToDtoUnit(allUnits));
        }
        return newUnitList;
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
    @org.springframework.transaction.annotation.Transactional
    public DtoUnit saveOneUnit(DtoUnit unit) {

        unitValidator.checkIfUnitAlreadyExists(unit);

        Unit pUnit = unitMapper.dtoUnitToUnit(unit);
        Unit dbUnit = unitRepository.save(pUnit);

        return unitMapper.unitToDtoUnit(dbUnit);
    }


    @Override
    @org.springframework.transaction.annotation.Transactional
    public DtoUnit updateOneUnit(Long id, DtoUnitIU newUnit) {
        Optional<Unit> optUnit = unitRepository.findById(id);

        if(optUnit.isPresent()){
            Unit foundUnit = optUnit.get();
            foundUnit.setUnitName(newUnit.getBirimIsim());

            Unit updatedUnit = unitRepository.save(foundUnit);

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
            // make personnel connected to Unit null
            unitRepository.detachPersonelFromUnit(optUnit.get());

            // delete unit
            unitRepository.delete(optUnit.get());
        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, unitId.toString()));
        }
    }


    @Override
    public Set<Personel> getPersonelsByUnitId(Long unitId) {
        Set<Personel> personels = new HashSet<>();

        Optional<Unit> optUnit = unitRepository.findById(unitId);

        if (optUnit.isPresent()) {
            personels.addAll(optUnit.get().getPersonels());
        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, unitId.toString()));
        }
        return personels;
    }

}

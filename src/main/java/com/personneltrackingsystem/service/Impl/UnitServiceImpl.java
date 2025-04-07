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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    private final UnitMapper unitMapper;


    // Solid example : article 1 (Single Responsibility Principle)

    @Override
    public Optional<Unit> findById(Long unitId) {
        return unitRepository.findById(unitId);
    }

    @Override
    public List<DtoUnit> getAllUnits(){
        List<Unit> unitList = unitRepository.findAll();

        return unitMapper.unitsToDtoUnits(unitList);
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
    public DtoUnit saveOneUnit(DtoUnitIU unitIU) {

        unitMapper.dtoUnitIUToUnit(unitIU);
        if(unitIU.getBirimIsim() != null){

            Unit pUnit = unitMapper.dtoUnitIUToUnit(unitIU);

            Unit dbUnit = unitRepository.save(pUnit);

            return unitMapper.unitToDtoUnit(dbUnit);
        }else{
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }
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

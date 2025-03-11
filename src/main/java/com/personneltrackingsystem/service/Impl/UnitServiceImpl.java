package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.repository.UnitRepository;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.UnitService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;


    private final PersonelRepository personelRepository;

    @Autowired
    public UnitServiceImpl(UnitRepository unitRepository, PersonelRepository personelRepository) {
        this.unitRepository = unitRepository;
        this.personelRepository = personelRepository;
    }


    // Solid example : article 1 (Single Responsibility Principle)
    @Override
    public List<DtoUnit> getAllUnits(){
        List<DtoUnit> dtoUnitList = new ArrayList<>();

        List<Unit> unitList =  unitRepository.findAll();
        for (Unit unit : unitList) {
            DtoUnit dto = new DtoUnit();
            BeanUtils.copyProperties(unit, dto);
            dtoUnitList.add(dto);
        }
        return dtoUnitList;
    }


    @Override
    public DtoUnit getOneUnit(Long unitId){
        DtoUnit dto = new DtoUnit();
        Optional<Unit> optional =  unitRepository.findById(unitId);
        if(optional.isPresent()){
            Unit dbUnit = optional.get();

            BeanUtils.copyProperties(dbUnit, dto);

            return dto;
        }
        else{
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, unitId.toString()));
        }

    }


    @Override
    public DtoUnit saveOneUnit(DtoUnitIU unit) {
        if(unit.getUnitName() != null){
            DtoUnit dto = new DtoUnit();
            Unit pUnit = new Unit();
            BeanUtils.copyProperties(unit, pUnit);

            Unit dbUnit = unitRepository.save(pUnit);
            BeanUtils.copyProperties(dbUnit, dto);

            return dto;
        }else{
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }
    }

    @Override
    public DtoUnit updateOneUnit(Long id, DtoUnitIU newUnit) {
        DtoUnit dto = new DtoUnit();

        Optional<Unit> gate = unitRepository.findById(id);

        if(gate.isPresent()){
            Unit foundUnit = gate.get();
            foundUnit.setUnitName(newUnit.getUnitName());

            Unit updatedUnit = unitRepository.save(foundUnit);

            BeanUtils.copyProperties(updatedUnit, dto);

            return dto;
        }else{
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString()));
        }

    }

    @Override
    public void deleteOneUnit(Long unitId) {
        Optional<Unit> unit = unitRepository.findById(unitId);

        if(unit.isPresent()){
            // Set the unit field of the associated personnel to null (if necessary)
            List<Personel> personels = personelRepository.findByUnit(unit.get());
            for (Personel personel : personels) {
                personel.setUnit(null);
            }
            personelRepository.saveAll(personels); // Save changes

            unitRepository.delete(unit.get());
        }
        else{
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, unitId.toString()));
        }



    }

    @Override
    public List<Personel> getPersonelsByUnitId(Long unitId){

        List<Personel> personels = new ArrayList<>();

        Optional<Unit> unit= unitRepository.findById(unitId);

        if(unit.isPresent()){
            for (Personel personel : unit.get().getPersonels()) {
                personels.add(personel);
            }
        }
        else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, unitId.toString()));
        }

        return personels;
    }


}

package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.repository.UnitRepository;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.IUnitService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UnitServiceImpl implements IUnitService {
    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private PersonelRepository personelRepository;


    /*
    public List<Unit> getAllUnits(Optional<Integer> unitId) {
        if(unitId.isPresent()){
            return unitRepository.findByUnitId(unitId.get());
        }
        else{
            return unitRepository.findAll();
        }
    }
    */

    @Override
    public List<Unit> getAllUnits(){
        return unitRepository.findAll();
    }


    @Override
    public Unit getOneUnit(Long unitId){
        return unitRepository.findById(unitId).orElse(null);
    }


    @Override
    public Unit saveOneUnit(Unit unit) {
        if(unit.getUnitName() != null){
            return unitRepository.save(unit);
        }else{
            return null;
        }
    }

    @Override
    public Unit updateOneUnit(Long id, Unit newUnit) {
        Optional<Unit> unit = unitRepository.findById(id);

        if(unit.isPresent()){
            Unit foundUnit = unit.get();
            foundUnit.setUnitName(newUnit.getUnitName());
            return unitRepository.save(foundUnit);
        }else{
            return null;
        }

    }

    @Override
    public void deleteOneUnit(Long unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found!"));

        // Set the unit field of the associated personnel to null (if necessary)
        List<Personel> personels = personelRepository.findByUnit(unit);
        for (Personel personel : personels) {
            personel.setUnit(null);
        }
        personelRepository.saveAll(personels); // Save changes

        unitRepository.delete(unit);
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
            new ResponseEntity<>("The unidId you entered could not be found!" , HttpStatus.BAD_REQUEST);
        }

        return personels;
    }


}

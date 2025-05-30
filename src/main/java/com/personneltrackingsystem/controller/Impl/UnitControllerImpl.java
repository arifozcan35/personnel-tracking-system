package com.personneltrackingsystem.controller.impl;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.controller.UnitController;
import com.personneltrackingsystem.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UnitControllerImpl implements UnitController {

    // solid example : article 3 (Liskov Substitution Principle)

    private final UnitService unitServiceImpl;


    @Override
    public List<DtoUnit> getAllUnits() {
        return unitServiceImpl.getAllUnits();
    }


    @Override
    public DtoUnit getOneUnit(Long unitId) {
        return unitServiceImpl.getOneUnit(unitId);
}


    @Override
    public DtoUnit createUnit(DtoUnitIU newUnit) {
        return unitServiceImpl.saveOneUnit(newUnit);
    }


    @Override
    public DtoUnit updateUnit(Long unitId, DtoUnitIU newUnit) {
        return unitServiceImpl.updateOneUnit(unitId, newUnit);
    }


    @Override
    public void deleteUnit(Long unitId) {
        unitServiceImpl.deleteOneUnit(unitId);
    }

}

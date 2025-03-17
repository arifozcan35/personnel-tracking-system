package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.controller.UnitController;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/unit")
public class UnitControllerImpl implements UnitController {

    // solid example : article 3 (Liskov Substitution Principle)

    private final UnitService unitServiceImpl;


    @GetMapping
    @Override
    public List<DtoUnit> getAllUnits() {
        return unitServiceImpl.getAllUnits();
    }

    @GetMapping("/{unitId}")
    @Override
    public DtoUnit getOneUnit(@PathVariable Long unitId) {
        return unitServiceImpl.getOneUnit(unitId);
}

    @PostMapping
    @Override
    public DtoUnit createUnit(@RequestBody DtoUnitIU newUnit) {
        return unitServiceImpl.saveOneUnit(newUnit);
    }

    @PutMapping("/{unitId}")
    @Override
    public DtoUnit updateUnit(@PathVariable Long unitId, @RequestBody DtoUnitIU newUnit) {
        return unitServiceImpl.updateOneUnit(unitId, newUnit);
    }
    @DeleteMapping("/{unitId}")
    @Override
    public void deleteUnit(@PathVariable Long unitId) {
        unitServiceImpl.deleteOneUnit(unitId);
    }


    @GetMapping("/personel/{unitId}")
    @Override
    public Set<Personel> getPersonels(@PathVariable Long unitId) {
        return unitServiceImpl.getPersonelsByUnitId(unitId);
    }
}

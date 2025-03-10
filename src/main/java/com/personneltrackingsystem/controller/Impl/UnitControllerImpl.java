package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.controller.IUnitController;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.service.IUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class UnitControllerImpl implements IUnitController {

    // solid example : article 3 (Liskov Substitution Principle)

    private final IUnitService unitServiceImpl;

    @Autowired
    public UnitControllerImpl(IUnitService unitServiceImpl){
        this.unitServiceImpl = unitServiceImpl;
    }

    @GetMapping
    @Override
    public List<DtoUnit> getAllUnits() {
        return unitServiceImpl.getAllUnits();
    }

/*
    @GetMapping("/{unitId}")
    public ResponseEntity<JSONObject> getUnitAndUnitPersonel(@PathVariable Long unitId) throws JSONException {
        return ResponseEntity.ok(unitService.getOneUnit(unitId));
    }
*/

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
    public List<Personel> getPersonels(@PathVariable Long unitId) {
        return unitServiceImpl.getPersonelsByUnitId(unitId);
    }
}

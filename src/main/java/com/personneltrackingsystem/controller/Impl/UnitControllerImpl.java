package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.IUnitController;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.service.IUnitService;
import com.personneltrackingsystem.service.Impl.UnitServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class UnitControllerImpl implements IUnitController {

    @Autowired
    private IUnitService unitServiceImpl;

    @GetMapping
    @Override
    public List<Unit> getAllUnits() {
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
    public Unit getOneUnit(@PathVariable Long unitId) {
        return unitServiceImpl.getOneUnit(unitId);
}

    @PostMapping
    @Override
    public Unit createUnit(@RequestBody Unit newUnit) {
        return unitServiceImpl.saveOneUnit(newUnit);
    }

    @PutMapping("/{unitId}")
    @Override
    public Unit updateUnit(@PathVariable Long unitId, @RequestBody Unit newUnit) {
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

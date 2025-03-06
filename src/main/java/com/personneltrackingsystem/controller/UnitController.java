package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class UnitController {
    @Autowired
    private UnitService unitService;

    @GetMapping
    public List<Unit> getAllUnits() {
        return unitService.getAllUnits();
    }

/*
    @GetMapping("/{unitId}")
    public ResponseEntity<JSONObject> getUnitAndUnitPersonel(@PathVariable Long unitId) throws JSONException {
        return ResponseEntity.ok(unitService.getOneUnit(unitId));
    }
*/

    @GetMapping("/{unitId}")
    public Unit getOneUnit(@PathVariable Long unitId) {
        return unitService.getOneUnit(unitId);
}

    @PostMapping
    public Unit createUnit(@RequestBody Unit newUnit) {
        return unitService.saveOneUnit(newUnit);
    }

    @PutMapping("/{unitId}")
    public Unit updateUnit(@PathVariable Long unitId, @RequestBody Unit newUnit) {
        return unitService.updateOneUnit(unitId, newUnit);
    }
    @DeleteMapping("/{unitId}")
    public void deleteUnit(@PathVariable Long unitId) {
        unitService.deleteOneUnit(unitId);
    }

    @GetMapping("/personel/{unitId}")
    public List<Personel> getPersonels(@PathVariable Long unitId) {
        return unitService.getPersonelsByUnitId(unitId);
    }
}

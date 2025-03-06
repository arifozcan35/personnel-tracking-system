package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.service.GateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gate")
public class GateController {
    @Autowired
    private GateService gateService;


    @GetMapping
    public List<Gate> getAllGates() {
        return gateService.getAllGates();
    }

    @GetMapping("/{gateId}")
    public Gate getOneGate(@PathVariable Long gateId) {
        return gateService.getOneGate(gateId);
    }

    @PostMapping
    public Gate createGate(@RequestBody Gate newGate) {
        return gateService.saveOneGate(newGate);
    }

    @PutMapping("/{gateId}")
    public Gate updateGate(@PathVariable Long gateId, @RequestBody Gate newGate) {
        return gateService.updateOneGate(gateId, newGate);
    }

    @DeleteMapping("/{gateId}")
    public void deleteGate(@PathVariable Long gateId) {
        gateService.deleteOneGate(gateId);
    }

    @PostMapping("/{wantedToEnterGate}")
    public ResponseEntity<String> passGise(@PathVariable Long wantedToEnterGate, @RequestBody Personel personel){
        return gateService.passGise(wantedToEnterGate, personel);
    }
}

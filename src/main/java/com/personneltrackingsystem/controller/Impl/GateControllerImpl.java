package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.IGateController;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.service.IGateService;
import com.personneltrackingsystem.service.Impl.GateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gate")
public class GateControllerImpl implements IGateController {

    @Autowired
    private IGateService gateService;


    @GetMapping
    @Override
    public List<Gate> getAllGates() {
        return gateService.getAllGates();
    }
    @GetMapping("/{gateId}")
    @Override
    public Gate getOneGate(@PathVariable Long gateId) {
        return gateService.getOneGate(gateId);
    }

    @PostMapping
    @Override
    public Gate createGate(@RequestBody Gate newGate) {
        return gateService.saveOneGate(newGate);
    }

    @PutMapping("/{gateId}")
    @Override
    public Gate updateGate(@PathVariable Long gateId, @RequestBody Gate newGate) {
        return gateService.updateOneGate(gateId, newGate);
    }

    @DeleteMapping("/{gateId}")
    @Override
    public void deleteGate(@PathVariable Long gateId) {
        gateService.deleteOneGate(gateId);
    }

    @PostMapping("/{wantedToEnterGate}")
    @Override
    public ResponseEntity<String> passGise(@PathVariable Long wantedToEnterGate, @RequestBody Personel personel){
        return gateService.passGise(wantedToEnterGate, personel);
    }
}

package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.controller.IGateController;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.service.IGateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gate")
public class GateControllerImpl implements IGateController {

    private final IGateService gateService;

    @Autowired
    public GateControllerImpl(IGateService gateService){
        this.gateService = gateService;
    }


    @GetMapping
    @Override
    public List<DtoGate> getAllGates() {
        return gateService.getAllGates();
    }
    @GetMapping("/{gateId}")
    @Override
    public DtoGate getOneGate(@PathVariable Long gateId) {
        return gateService.getOneGate(gateId);
    }

    @PostMapping
    @Override
    public DtoGate createGate(@RequestBody DtoGateIU newGate) {
        return gateService.saveOneGate(newGate);
    }

    @PutMapping("/{gateId}")
    @Override
    public DtoGate updateGate(@PathVariable Long gateId, @RequestBody DtoGateIU newGate) {
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

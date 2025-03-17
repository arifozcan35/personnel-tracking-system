package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.controller.GateController;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.service.GateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gate")
public class GateControllerImpl implements GateController {

    private final GateService gateServiceImpl;

    @GetMapping
    @Override
    public List<DtoGate> getAllGates() {
        return gateServiceImpl.getAllGates();
    }
    @GetMapping("/{gateId}")
    @Override
    public DtoGate getOneGate(@PathVariable Long gateId) {
        return gateServiceImpl.getOneGate(gateId);
    }

    @PostMapping
    @Override
    public DtoGate createGate(@RequestBody DtoGateIU newGate) {
        return gateServiceImpl.saveOneGate(newGate);
    }

    @PutMapping("/{gateId}")
    @Override
    public DtoGate updateGate(@PathVariable Long gateId, @RequestBody DtoGateIU newGate) {
        return gateServiceImpl.updateOneGate(gateId, newGate);
    }

    @DeleteMapping("/{gateId}")
    @Override
    public void deleteGate(@PathVariable Long gateId) {
        gateServiceImpl.deleteOneGate(gateId);
    }


    @GetMapping("/personel/{gateId}")
    @Override
    public Set<Personel> getPersonels(@PathVariable Long gateId) {
        return gateServiceImpl.getPersonelsByGateId(gateId);
    }

    @PostMapping("personelpass/{wantedToEnterGate}")
    @Override
    public ResponseEntity<String> passGate(@PathVariable Long wantedToEnterGate, @RequestBody Personel personel){
        return gateServiceImpl.passGate(wantedToEnterGate, personel);
    }
}

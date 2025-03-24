package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.controller.GateController;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.service.GateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Gate Controller", description = "Gate CRUD operations and personnel listing by gate")
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



    @Operation(
            summary = "Personnel by gates",
            description = "Lists all personnel with the given gate number."
    )
    @GetMapping("/personel/{gateId}")
    @Override
    public Set<Personel> getPersonels(@PathVariable Long gateId) {
        return gateServiceImpl.getPersonelsByGateId(gateId);
    }


    @Operation(
            summary = "Entry Permit Check",
            description = "Checking whether the given personnel has permission to enter the gate they want to enter."
    )
    @PostMapping("personelpass/{wantedToEnterGate}")
    @Override
    public ResponseEntity<String> passGate(@PathVariable Long wantedToEnterGate, @RequestBody Long personelId){
        return gateServiceImpl.passGate(wantedToEnterGate, personelId);
    }
}

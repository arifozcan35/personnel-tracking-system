package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Personel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Gate Controller", description = "Gate CRUD operations and personnel listing by gate")
@RequestMapping("/api/gate")
public interface GateController {

    @GetMapping
    List<DtoGate> getAllGates();

    @GetMapping("/{gateId}")
    DtoGate getOneGate(@PathVariable Long gateId);

    @PostMapping
    DtoGate createGate(@RequestBody DtoGateIU newGate);

    @PutMapping("/{gateId}")
    DtoGate updateGate(@PathVariable Long gateId, @RequestBody DtoGateIU newGate);

    @DeleteMapping("/{gateId}")
    void deleteGate(@PathVariable Long gateId);


    @Operation(
            summary = "Personnel by gates",
            description = "Lists all personnel with the given gate number."
    )
    @GetMapping("/personel/{gateId}")
    Set<Personel> getPersonels(@PathVariable Long gateId);

    @Operation(
            summary = "Entry Permit Check",
            description = "Checking whether the given personnel has permission to enter the gate they want to enter."
    )
    @PostMapping("personelpass/{wantedToEnterGate}")
    ResponseEntity<String> passGate(@PathVariable Long wantedToEnterGate, @RequestBody Long personelId);
}

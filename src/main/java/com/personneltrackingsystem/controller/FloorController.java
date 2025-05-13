package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.dto.FloorCreateUpdateRequestDTO;
import com.personneltrackingsystem.dto.FloorResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Floor Controller", description = "Floor CRUD operations and personnel listing by floor")
@RequestMapping("/api/floor")
public interface FloorController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<FloorResponseDTO> getAllFloors();

    @GetMapping("/{floorId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    FloorResponseDTO getOneFloor(@PathVariable Long floorId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    FloorResponseDTO createFloor(@RequestBody FloorCreateUpdateRequestDTO newFloor);

    @PutMapping("/{floorId}")
    @PreAuthorize("hasRole('ADMIN')")
    FloorResponseDTO updateFloor(@PathVariable Long floorId, @RequestBody FloorCreateUpdateRequestDTO newFloor);

    @DeleteMapping("/{floorId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteFloor(@PathVariable Long floorId);



    @Operation(
            summary = "Entry Permit Check",
            description = "Checking whether the given personnel has permission to enter the gate they want to enter."
    )
    @PostMapping("personelpass/{wantedToEnterFloor}")
    ResponseEntity<String> passFloor(@PathVariable Long wantedToEnterFloor, @RequestBody Long personelId);
}

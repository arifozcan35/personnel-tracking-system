package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Gate Controller", description = "Gate CRUD operations")
@RequestMapping("/api/gate")
public interface GateController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoGate> getAllGates();

    @GetMapping("/{gateId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    DtoGate getOneGate(@PathVariable Long gateId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    DtoGate createGate(@RequestBody DtoGate newGate);

    @PutMapping("/{gateId}")
    @PreAuthorize("hasRole('ADMIN')")
    DtoGate updateGate(@PathVariable Long gateId, @RequestBody DtoGateIU newGate);

    @DeleteMapping("/{gateId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteGate(@PathVariable Long gateId);
} 
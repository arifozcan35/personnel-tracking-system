package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoBuilding;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Building Controller", description = "Building CRUD operations")
@RequestMapping("/api/building")
public interface BuildingController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoBuilding> getAllBuildings();

    @GetMapping("/{buildingId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    DtoBuilding getOneBuilding(@PathVariable Long buildingId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    DtoBuilding createBuilding(@RequestBody DtoBuilding newBuilding);

    @PutMapping("/{buildingId}")
    @PreAuthorize("hasRole('ADMIN')")
    DtoBuilding updateBuilding(@PathVariable Long buildingId, @RequestBody DtoBuilding newBuilding);

    @DeleteMapping("/{buildingId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteBuilding(@PathVariable Long buildingId);
} 
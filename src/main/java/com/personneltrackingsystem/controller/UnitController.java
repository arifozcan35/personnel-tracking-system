package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Unit Controller", description = "Unit CRUD operations and personnel listing by unit")
@RequestMapping("/api/unit")
public interface UnitController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoUnit> getAllUnits();

    @GetMapping("/{unitId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    DtoUnit getOneUnit(@PathVariable Long unitId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    DtoUnit createUnit(@RequestBody DtoUnit newUnit);

    @PutMapping("/{unitId}")
    @PreAuthorize("hasRole('ADMIN')")
    DtoUnit updateUnit(@PathVariable Long unitId, @RequestBody DtoUnitIU newUnit);

    @DeleteMapping("/{unitId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteUnit(@PathVariable Long unitId);

}

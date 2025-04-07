package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Personel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Unit Controller", description = "Unit CRUD operations and personnel listing by unit")
@RequestMapping("/api/unit")
public interface UnitController {

    @GetMapping
    List<DtoUnit> getAllUnits();

    @GetMapping("/{unitId}")
    DtoUnit getOneUnit(@PathVariable Long unitId);

    @PostMapping
    DtoUnit createUnit(@RequestBody DtoUnitIU newUnit);

    @PutMapping("/{unitId}")
    DtoUnit updateUnit(@PathVariable Long unitId, @RequestBody DtoUnitIU newUnit);

    @DeleteMapping("/{unitId}")
    void deleteUnit(@PathVariable Long unitId);

    @Operation(
            summary = "Personnel by units",
            description = "Lists all personnel with the given unit number."
    )
    @GetMapping("/personel/{unitId}")
    Set<Personel> getPersonels(@PathVariable Long unitId);
}

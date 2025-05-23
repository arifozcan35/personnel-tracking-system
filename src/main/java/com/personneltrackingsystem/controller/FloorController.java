package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoFloor;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Floor Controller", description = "Floor CRUD operations and personnel listing by floor")
@RequestMapping("/api/floor")
public interface FloorController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoFloor> getAllFloors();

    @GetMapping("/{floorId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    DtoFloor getOneFloor(@PathVariable Long floorId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    DtoFloor createFloor(@RequestBody DtoFloor newFloor);

    @PutMapping("/{floorId}")
    @PreAuthorize("hasRole('ADMIN')")
    DtoFloor updateFloor(@PathVariable Long floorId, @RequestBody DtoFloor newFloor);

    @DeleteMapping("/{floorId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteFloor(@PathVariable Long floorId);

}

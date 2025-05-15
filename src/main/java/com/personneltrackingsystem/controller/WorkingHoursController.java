package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoWorkingHours;
import com.personneltrackingsystem.dto.DtoWorkingHoursIU;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Working Hours Controller", description = "Working Hours CRUD operations")
@RequestMapping("/api/working-hours")
public interface WorkingHoursController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoWorkingHours> getAllWorkingHours();

    @GetMapping("/{workingHoursId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    DtoWorkingHours getOneWorkingHours(@PathVariable Long workingHoursId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    DtoWorkingHours createWorkingHours(@RequestBody DtoWorkingHours newWorkingHours);

    @PutMapping("/{workingHoursId}")
    @PreAuthorize("hasRole('ADMIN')")
    DtoWorkingHours updateWorkingHours(@PathVariable Long workingHoursId, @RequestBody DtoWorkingHoursIU newWorkingHours);

    @DeleteMapping("/{workingHoursId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteWorkingHours(@PathVariable Long workingHoursId);
} 
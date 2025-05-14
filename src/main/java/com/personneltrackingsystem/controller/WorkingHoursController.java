package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoWork;
import com.personneltrackingsystem.dto.DtoWorkIU;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Working Hours Controller", description = "Working Hours CRUD operations")
@RequestMapping("/api/working-hours")
public interface WorkingHoursController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoWork> getAllWorkingHours();

    @GetMapping("/{workingHoursId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    DtoWork getOneWorkingHours(@PathVariable Long workingHoursId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    DtoWork createWorkingHours(@RequestBody DtoWorkIU newWorkingHours);

    @PutMapping("/{workingHoursId}")
    @PreAuthorize("hasRole('ADMIN')")
    DtoWork updateWorkingHours(@PathVariable Long workingHoursId, @RequestBody DtoWorkIU newWorkingHours);

    @DeleteMapping("/{workingHoursId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteWorkingHours(@PathVariable Long workingHoursId);
} 
package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoPersonelType;
import com.personneltrackingsystem.dto.DtoPersonelTypeIU;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Personel Type Controller", description = "Personel Type CRUD operations")
@RequestMapping("/api/personel-type")
public interface PersonelTypeController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoPersonelType> getAllPersonelTypes();

    @GetMapping("/{personelTypeId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    DtoPersonelType getOnePersonelType(@PathVariable Long personelTypeId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    DtoPersonelType createPersonelType(@RequestBody DtoPersonelType newPersonelType);

    @PutMapping("/{personelTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    DtoPersonelType updatePersonelType(@PathVariable Long personelTypeId, @RequestBody DtoPersonelTypeIU newPersonelType);

    @DeleteMapping("/{personelTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deletePersonelType(@PathVariable Long personelTypeId);
} 
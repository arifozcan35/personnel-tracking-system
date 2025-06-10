package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelAll;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Personel Controller", description = "Personnel CRUD operations")
@RequestMapping("/api/personel")
public interface PersonelController {

    // solid example : article 4 (Interface Substitution Principle)

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    List<DtoPersonel> getAllPersonels();

    @GetMapping("/{personelId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    DtoPersonelAll getOnePersonel(@PathVariable Long personelId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> createPersonel(@RequestBody DtoPersonelIU newPersonel);

    @PutMapping("/{personelId}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> updatePersonel(@PathVariable Long personelId, @RequestBody DtoPersonelIU newPersonel);

    @DeleteMapping("/{personelId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deletePersonel(@PathVariable Long personelId);

    @Operation(
            summary = "Personnel by units",
            description = "Lists all personnel with the given unit number."
    )
    @GetMapping("/personel/{unitId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    Set<DtoPersonel> getPersonelsByUnit(@PathVariable Long unitId);

}

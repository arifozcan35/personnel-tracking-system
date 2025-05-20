package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelAll;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Personel Controller", description = "Personnel CRUD operations")
@RequestMapping("/api/personel")
public interface PersonelController {

    // solid example : article 4 (Interface Substitution Principle)

    @GetMapping
    List<DtoPersonel> getAllPersonels();

    @GetMapping("/{personelId}")
    DtoPersonelAll getOnePersonel(@PathVariable Long personelId);

    @PostMapping
    ResponseEntity<String> createPersonel(@RequestBody DtoPersonelIU newPersonel);

    @PutMapping("/{personelId}")
    ResponseEntity<String> updatePersonel(@PathVariable Long personelId, @RequestBody DtoPersonelIU newPersonel);

    @DeleteMapping("/{personelId}")
    void deletePersonel(@PathVariable Long personelId);

    @Operation(
            summary = "Personnel by units",
            description = "Lists all personnel with the given unit number."
    )
    @GetMapping("/personel/{unitId}")
    Set<DtoPersonel> getPersonelsByUnit(@PathVariable Long unitId);

}

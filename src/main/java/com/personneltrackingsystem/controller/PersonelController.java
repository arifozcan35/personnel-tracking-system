package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.entity.Work;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Personel Controller", description = "Personnel CRUD operations, working hours validity check and salary transactions")
@RequestMapping("/api/personel")
public interface PersonelController {

    // solid example : article 4 (Interface Substitution Principle)

    @GetMapping
    List<DtoPersonel> getAllPersonels();

    @GetMapping("/{personelId}")
    DtoPersonel getOnePersonel(@PathVariable Long personelId);

    @PostMapping
    ResponseEntity<String> createPersonel(@RequestBody DtoPersonelIU newPersonel);

    @PutMapping("/{personelId}")
    ResponseEntity<String> updatePersonel(@PathVariable Long personelId, @RequestBody DtoPersonelIU newPersonel);

    @DeleteMapping("/{personelId}")
    void deletePersonel(@PathVariable Long personelId);


    @Operation(
            summary = "Working hour calculation",
            description = "Calculates whether the working hours of the personnel whose ID is given are valid."
    )
    @PostMapping("/salary/{personelId}")
    DtoPersonel seeSalary(@PathVariable Long personelId);

    @Operation(
            summary = "Viewing working hours information",
            description = "The working hours information of the personnel belonging to the given id is displayed."
    )
    @GetMapping("/work/{personelId}")
    Work seeWork(@PathVariable Long personelId);

    @Operation(
            summary = "Salary information",
            description = "Salary information of all personnel in the system is displayed."
    )
    @GetMapping("/salaries")
    Map<String, Double> getAllSalaries();
}

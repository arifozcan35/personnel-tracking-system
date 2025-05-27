package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Turnstile Controller", description = "Turnstile CRUD operations")
@RequestMapping("/api/turnstile")
public interface TurnstileController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoTurnstile> getAllTurnstiles();


    @GetMapping("/{turnstileId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    DtoTurnstile getOneTurnstile(@PathVariable Long turnstileId);


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    DtoTurnstile createTurnstile(@RequestBody DtoTurnstileIU newTurnstile);


    @PutMapping("/{turnstileId}")
    @PreAuthorize("hasRole('ADMIN')")
    DtoTurnstile updateTurnstile(@PathVariable Long turnstileId, @RequestBody DtoTurnstileIU newTurnstile);


    @DeleteMapping("/{turnstileId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteTurnstile(@PathVariable Long turnstileId);


    @Operation(
            summary = "Entry Permit Check",
            description = "Checking whether the given personnel has the authority to enter through the turnstile they want to enter."
    )
    @PostMapping("personelpass/{wantedToEnterTurnstile}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    ResponseEntity<String> passTurnstile(@PathVariable("wantedToEnterTurnstile") Long wantedToEnterTurnstile, @RequestBody Long personelId);


    @Operation(
            summary = "Get Daily Personnel List",
            description = "Get the list of all personnel who passed through turnstiles on a specific date. Uses Hazelcast cache for performance."
    )
    @GetMapping("/daily-personnel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoDailyPersonnelEntry> getDailyPersonnelList(
            @Parameter(description = "Date in YYYY-MM-DD format", example = "2025-05-26")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            LocalDate date
    );

    @Operation(
            summary = "Get Daily Personnel List with Redis",
            description = "Get the list of all personnel who passed through turnstiles on a specific date. Uses Redis cache for performance."
    )
    @GetMapping("/daily-personnel-redis")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoDailyPersonnelEntry> getDailyPersonnelListWithRedis(
            @Parameter(description = "Date in YYYY-MM-DD format", example = "2025-05-26")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            LocalDate date
    );
} 
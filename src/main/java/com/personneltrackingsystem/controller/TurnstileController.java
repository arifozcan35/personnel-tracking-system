package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;
import com.personneltrackingsystem.dto.DtoTurnstilePassageRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.HashMap;
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
    ResponseEntity<String> passTurnstile(
        @PathVariable("wantedToEnterTurnstile") Long wantedToEnterTurnstile, 
        @RequestBody DtoTurnstilePassageRequest request,
        @RequestParam(required = false, defaultValue = "2025-05-29 09:17:35") String operationTimeStr
    );

    @Operation(
            summary = "Get Monthly Main Entrance Personnel List With Hazelcast",
            description = "Get the list of all personnel who passed through main entrance turnstiles for a specific month. Data is organized by date. Uses Hazelcast cache for performance."
    )
    @GetMapping("/monthly-personnel-list-hazelcast")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    HashMap<String, List<DtoDailyPersonnelEntry>> getMonthlyMainEntrancePersonnelListWithHazelcast(
            @Parameter(description = "Year and month in YYYY-MM format", example = "2025-05")
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM") 
            YearMonth yearMonth
    );
    
    @Operation(
            summary = "Get Monthly Main Entrance Personnel List with Redis",
            description = "Get the list of all personnel who passed through main entrance turnstiles for a specific month. Data is organized by date. Uses Redis cache for performance."
    )
    @GetMapping("/monthly-main-entrance-personnel-redis")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    HashMap<String, List<DtoDailyPersonnelEntry>> getMonthlyMainEntrancePersonnelListWithRedis(
            @Parameter(description = "Year and month in YYYY-MM format", example = "2025-05")
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM") 
            YearMonth yearMonth
    );
} 
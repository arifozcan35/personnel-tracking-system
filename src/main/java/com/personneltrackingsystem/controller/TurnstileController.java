package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;
import com.personneltrackingsystem.dto.DtoTurnstilePassageFullRequest;

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
import java.util.Map;

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
    @PostMapping("/personelpass")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    ResponseEntity<String> passTurnstile(@RequestBody DtoTurnstilePassageFullRequest request);

    
    @Operation(
            summary = "Get Turnstile-Based Monthly Personnel List With Hazelcast",
            description = "Get the list of all personnel who passed through any turnstile for a specific month. Data is organized by date and then by turnstile. Uses Hazelcast cache for performance."
    )
    @GetMapping("/turnstile-based-monthly-personnel-list-hazelcast")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getTurnstileBasedMonthlyPersonnelListWithHazelcast(
            @Parameter(description = "Year and month in YYYY-MM format", example = "2025-05")
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM") 
            YearMonth yearMonth
    );
    

    @Operation(
            summary = "Get Turnstile-Based Monthly Personnel List With Redis",
            description = "Get the list of all personnel who passed through any turnstile for a specific month. Data is organized by date and then by turnstile. Uses only Redis data built from daily records transferred at midnight, not from the database."
    )
    @GetMapping("/turnstile-based-monthly-personnel-list-redis")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getTurnstileBasedMonthlyPersonnelListWithRedis(
            @Parameter(description = "Year and month in YYYY-MM format", example = "2025-05")
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM") 
            YearMonth yearMonth
    );

    @Operation(
            summary = "Get Daily Turnstile Passage Records",
            description = "Get the current day's turnstile passage records from Redis. Data is organized by turnstile name."
    )
    @GetMapping("/daily-turnstile-passage-records")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    Map<String, List<DtoTurnstileBasedPersonnelEntry>> getDailyTurnstilePassageRecords();
} 
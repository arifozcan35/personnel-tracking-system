package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
} 
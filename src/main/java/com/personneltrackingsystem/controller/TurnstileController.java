package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import io.swagger.v3.oas.annotations.tags.Tag;
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
} 
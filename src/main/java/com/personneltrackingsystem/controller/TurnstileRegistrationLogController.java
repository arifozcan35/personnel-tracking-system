package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLog;
import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Turnstile Registration Log Controller", description = "Turnstile Registration Log CRUD operations")
@RequestMapping("/api/turnstile-registration-log")
public interface TurnstileRegistrationLogController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<DtoTurnstileRegistrationLog> getAllTurnstileRegistrationLogs();

    @GetMapping("/{logId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    DtoTurnstileRegistrationLog getOneTurnstileRegistrationLog(@PathVariable Long logId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    DtoTurnstileRegistrationLog createTurnstileRegistrationLog(@RequestBody DtoTurnstileRegistrationLogIU newLog);

    @PutMapping("/{logId}")
    @PreAuthorize("hasRole('ADMIN')")
    DtoTurnstileRegistrationLog updateTurnstileRegistrationLog(@PathVariable Long logId, @RequestBody DtoTurnstileRegistrationLogIU newLog);

    @DeleteMapping("/{logId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteTurnstileRegistrationLog(@PathVariable Long logId);
} 
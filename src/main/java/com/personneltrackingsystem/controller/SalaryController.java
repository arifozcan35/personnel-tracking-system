package com.personneltrackingsystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.personneltrackingsystem.dto.DtoSalary;
import com.personneltrackingsystem.dto.DtoSalaryCalculationRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Salary Controller", description = "Salary operations")
@RequestMapping("/api/salaries")
public interface SalaryController {
    
    @Operation(summary = "Get an personnel's entire salary history")
    @ApiResponse(responseCode = "200", description = "Personnel salary history successfully retrieved")
    @GetMapping("/personnel/{personelId}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<DtoSalary>> getSalaryHistoryForPersonel(@PathVariable Long personelId);
    
    @Operation(summary = "Get a personnel's salary for a specific month")
    @ApiResponse(responseCode = "200", description = "Personnel's salary successfully retrieved")
    @GetMapping("/personnel/{personelId}/month/{yearMonth}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<DtoSalary> getSalaryForPersonelAndMonth(@PathVariable Long personelId, @PathVariable String yearMonth);
    
    @Operation(summary = "Get all salaries for a specific month")
    @ApiResponse(responseCode = "200", description = "All salaries for the specified month successfully retrieved")
    @GetMapping("/month/{yearMonth}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<DtoSalary>> getAllSalariesForMonth(@PathVariable String yearMonth);
    
    @Operation(
        summary = "Calculate salaries for a specific month",
        description = "Calculates salaries for the specified month and personnel",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Salary calculation request",
            content = @Content(
                schema = @Schema(implementation = DtoSalaryCalculationRequest.class),
                examples = @ExampleObject(
                    name = "Basic salary calculation request",
                    value = "{\n" +
                           "  \"year\": 2025,\n" +
                           "  \"monthNumber\": 6,\n" +
                           "  \"personelId\": 1,\n" +
                           "  \"forceRecalculation\": false\n" +
                           "}"
                )
            )
        )
    )
    
    @ApiResponse(responseCode = "200", description = "Salary calculation process successfully completed")
    @PostMapping("/calculate")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<DtoSalary>> calculateSalaries(@RequestBody DtoSalaryCalculationRequest request);
} 
package com.personneltrackingsystem.dto;

import java.time.YearMonth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoSalaryCalculationRequest {
    
    @Schema(description = "Year of the salary calculation", example = "2025")
    private Integer year;
    
    @Schema(description = "Month of the salary calculation (1-12)", example = "6")
    private Integer monthNumber;
    
    @Schema(description = "Personel ID to calculate the salary (if null, all personnel will be calculated)", example = "1")
    private Long personelId; 
    
    @Schema(description = "Whether to overwrite existing calculations (if true, existing calculations will be overwritten)", example = "false")
    private Boolean forceRecalculation = false; 
    
    public YearMonth getYearMonth() {
        if (year == null || monthNumber == null) {
            return null;
        }
        
        if (monthNumber < 1 || monthNumber > 12) {
            throw new IllegalArgumentException("Month value must be between 1 and 12");
        }
        
        return YearMonth.of(year, monthNumber);
    }
} 
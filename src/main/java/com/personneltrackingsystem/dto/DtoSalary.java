package com.personneltrackingsystem.dto;

import java.time.LocalDate;
import java.time.YearMonth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoSalary {
    
    @Schema(description = "Salary ID", example = "1")
    private Long salaryId;

    @Schema(description = "Personel ID", example = "1")
    private Long personelId;

    @Schema(description = "Personel Name", example = "Arif Ozcan")
    private String personelName;

    @Schema(description = "Personel Email", example = "zcanarif@gmail.com")
    private String personelEmail;

    @Schema(description = "Personel Type Name", example = "Staff")
    private String personelTypeName;

    @Schema(description = "Salary Month", example = "2025-06")
    private YearMonth salaryMonth;

    @Schema(description = "Base Amount", example = "50000")
    private Double baseAmount;

    @Schema(description = "Late Days", example = "2")
    private Integer lateDays;

    @Schema(description = "Late Penalty Amount", example = "1200")
    private Double latePenaltyAmount;

    @Schema(description = "Final Amount", example = "48800")
    private Double finalAmount;

    @Schema(description = "Calculation Date", example = "2025-06-10")
    private LocalDate calculationDate;

    @Schema(description = "Is Paid", example = "false")
    private Boolean isPaid;
} 
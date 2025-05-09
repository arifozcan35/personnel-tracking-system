package com.personneltrackingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkValidationEventDto {
    private Long personelId;
    private String personelName;
    private String personelEmail;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Boolean isWorkValid;
    private LocalDateTime validationTime;
    private Boolean isAdministrator;
    private Double salary;
    private Double penaltyAmount;
} 
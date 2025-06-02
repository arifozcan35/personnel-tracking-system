package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.OperationType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoTurnstileBasedPersonnelEntry {

    private Long personelId;

    private String personelName;

    private String personelEmail;

    private LocalDateTime operationTime;
    
    private OperationType operationType;
} 
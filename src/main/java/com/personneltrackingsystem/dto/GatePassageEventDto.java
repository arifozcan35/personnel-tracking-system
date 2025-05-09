package com.personneltrackingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GatePassageEventDto {
    private Long personelId;
    private String personelName;
    private String personelEmail;
    private Long gateId;
    private String gateName;
    private LocalDateTime passageTime;
} 
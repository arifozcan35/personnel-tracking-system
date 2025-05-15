package com.personneltrackingsystem.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoTurnstileRegistrationLog {

    private LocalDateTime operationTime;

    private String operationType;
}

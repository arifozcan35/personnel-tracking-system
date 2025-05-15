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
public class DtoTurnstileRegistrationLogIU {

    private Long turnstileRegistrationLogId;

    private Long personelId;

    private Long turnstileId;

    private LocalDateTime operationTime;

    private String operationType;
}

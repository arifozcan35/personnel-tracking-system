package com.personneltrackingsystem.dto;

import java.time.LocalDateTime;

import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Turnstile;

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

    private Personel personelId;

    private Turnstile turnstileId;

    private LocalDateTime operationTime;

    private String operationType;
}

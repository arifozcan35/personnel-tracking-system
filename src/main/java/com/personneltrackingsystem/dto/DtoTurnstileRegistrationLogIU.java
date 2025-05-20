package com.personneltrackingsystem.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoTurnstileRegistrationLogIU {

    @Schema(description = "The id of personel", example = "3")
    private Long personelId;

    @Schema(description = "The id of turnstile", example = "5")
    private Long turnstileId;

    @Schema(description = "The operation time", example = "2021-01-01T00:00:00")
    private LocalDateTime operationTime;

    @Schema(description = "The operation type", example = "IN")
    private String operationType;
}

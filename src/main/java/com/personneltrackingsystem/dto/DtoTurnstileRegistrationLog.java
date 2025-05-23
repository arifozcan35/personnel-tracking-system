package com.personneltrackingsystem.dto;

import java.time.LocalDateTime;

import com.personneltrackingsystem.entity.OperationType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoTurnstileRegistrationLog {

    @Schema(description = "The operation time", example = "2021-01-01T00:00:00")
    private LocalDateTime operationTime;

    @Schema(description = "The operation type", example = "IN")
    private OperationType operationType;
}

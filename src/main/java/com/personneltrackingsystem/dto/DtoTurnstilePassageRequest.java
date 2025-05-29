package com.personneltrackingsystem.dto;

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
public class DtoTurnstilePassageRequest {

    @Schema(description = "The id of personel", example = "3")
    private Long personelId;

    @Schema(description = "The operation type (IN or OUT)", example = "IN")
    private OperationType operationType;
} 
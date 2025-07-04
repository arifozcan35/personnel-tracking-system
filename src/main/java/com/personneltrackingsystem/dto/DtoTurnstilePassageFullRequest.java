package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.OperationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoTurnstilePassageFullRequest {
    
    @Schema(description = "Wanted to enter turnstile ID", example = "20")
    private Long wantedToEnterTurnstileId;
    
    @Schema(description = "Personnel ID", example = "1")
    private Long personelId;
    
    @Schema(description = "Operation type (IN/OUT)", example = "IN")
    private OperationType operationType;
    
    @Schema(description = "Operation time in format HH:mm:ss (current date will be automatically applied)", example = "09:17:35")
    private String operationTimeStr;
} 
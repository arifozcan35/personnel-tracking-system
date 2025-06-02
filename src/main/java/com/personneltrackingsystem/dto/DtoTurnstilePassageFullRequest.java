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
    
    @Schema(description = "Wanted to enter turnstile ID", example = "20", required = true)
    private Long wantedToEnterTurnstileId;
    
    @Schema(description = "Personnel ID", example = "1", required = true)
    private Long personelId;
    
    @Schema(description = "Operation type (IN/OUT)", example = "IN", required = true)
    private OperationType operationType;
    
    @Schema(description = "Operation time in format yyyy-MM-dd HH:mm:ss", example = "2025-05-29 09:17:35", required = false)
    private String operationTimeStr;
} 
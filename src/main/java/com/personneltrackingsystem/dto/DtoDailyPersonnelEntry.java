package com.personneltrackingsystem.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.personneltrackingsystem.entity.OperationType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Data;

@Data
@NoArgsConstructor
public class DtoDailyPersonnelEntry {

    @Schema(description = "Personnel ID", example = "1")
    private Long personelId;

    @Schema(description = "Personnel Name", example = "Arif Ozcan")
    private String personelName;

    @Schema(description = "Personnel Email", example = "zcanarif@gmail.com")
    private String personelEmail;

    @Schema(description = "First entry time of the day", example = "2025-05-26T09:10:00")
    private LocalDateTime firstEntryTime;

    @Schema(description = "Last exit time of the day", example = "2025-05-26T18:00:00")
    private LocalDateTime lastExitTime;

    @Schema(description = "Current status", example = "IN")
    private OperationType currentStatus;

    @Schema(description = "List of all turnstile passages during the day")
    private List<DtoTurnstilePassage> passages;

    @Data
    @NoArgsConstructor
    public static class DtoTurnstilePassage {
        
        @Schema(description = "Turnstile ID", example = "1")
        private Long turnstileId;

        @Schema(description = "Turnstile Name", example = "Main Entrance")
        private String turnstileName;

        @Schema(description = "Operation Time", example = "2025-05-26T09:15:00")
        private LocalDateTime operationTime;

        @Schema(description = "Operation Type", example = "IN")
        private OperationType operationType;
    }
} 
package com.personneltrackingsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoWork {

    @Schema(description = "Entry time for work", example = "09:18")
    private LocalTime checkInTime;

    @Schema(description = "Exit time for work", example = "18:00")
    private LocalTime checkOutTime;

}

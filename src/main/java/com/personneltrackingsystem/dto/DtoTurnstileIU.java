package com.personneltrackingsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoTurnstileIU {

    @Schema(description = "The name of turnstile", example = "Turnstile 1")
    private String turnstileName;

    @Schema(description = "The id of gate", example = "1")
    private Long gateId;
}

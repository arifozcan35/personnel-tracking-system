package com.personneltrackingsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Schema(description = "The username of the user", example = "arifozcan")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "The password of the user", example = "gib6")
    private String password;
}

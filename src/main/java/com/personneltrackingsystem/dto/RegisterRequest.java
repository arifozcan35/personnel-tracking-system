package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
    @Schema(description = "The full name of the user", example = "Arif Ozcan")
    private String name;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 15, message = "Username must be between 3 and 15 characters")
    @Schema(description = "The username of the user", example = "arifozcan")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 3, message = "Password must be at least 3 characters")
    @Schema(description = "The password of the user", example = "gib6")
    private String password;

    @NotBlank(message = "Email is required")
    @Schema(description = "The email of the user", example = "zcanarif@gmail.com")
    private String email;

    @Schema(description = "The enabled status of the user", example = "true")
    private boolean enabled;

    @Schema(description = "The role of the user", example = "ADMIN")
    private Role role;
}
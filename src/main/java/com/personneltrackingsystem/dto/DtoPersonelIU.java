package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.Work;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoPersonelIU {

    @Schema(description = "The name of Personnel", example = "Arif Ozcan")
    private String name;

    @Schema(description = "The e-mail of Personnel", example = "zcanarif@gmail.com")
    private String email;

    @Schema(description = "Staff position", example = "0")
    private Boolean administrator;

    @Schema(description = "The salary amount of Personnel", example = "25000")
    private Double salary;


    private DtoUnit unit;

    private DtoGate gate;

    private Work work;
}

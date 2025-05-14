package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.Gate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoTurnstileIU {

    private Long turnstileId;

    private String turnstileName;

    private Gate gateId;
}

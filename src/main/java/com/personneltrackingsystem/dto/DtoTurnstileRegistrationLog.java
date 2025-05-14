package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Turnstile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoTurnstileRegistrationLog {

    private Personel personelId;

    private Turnstile turnstileId;
}

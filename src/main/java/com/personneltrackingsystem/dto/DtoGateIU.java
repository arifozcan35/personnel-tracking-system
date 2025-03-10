package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.Personel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoGateIU {
    private Long gateId;

    private String gateName;

    private List<DtoPersonel> personels;
}

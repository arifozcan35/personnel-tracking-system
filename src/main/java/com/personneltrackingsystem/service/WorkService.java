package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Work;

public interface WorkService {

    public Work getOneWorkofPersonel(Long personelId);

    public DtoPersonel workHoursCalculate(Long personelId);

    public void workHoursCalculate2(Personel newPersonel);
}

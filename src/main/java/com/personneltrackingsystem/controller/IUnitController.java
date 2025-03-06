package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IUnitController {

    public List<Unit> getAllUnits();

    public Unit getOneUnit(Long unitId);

    public Unit createUnit(Unit newUnit);

    public Unit updateUnit(Long unitId, Unit newUnit);

    public void deleteUnit(Long unitId);

    public List<Personel> getPersonels(Long unitId);
}

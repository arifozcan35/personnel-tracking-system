package com.personneltrackingsystem.service;

import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;

import java.util.List;

public interface IUnitService {

    public List<Unit> getAllUnits();

    public Unit getOneUnit(Long unitId);

    public Unit saveOneUnit(Unit unit);

    public Unit updateOneUnit(Long id, Unit newUnit);

    public void deleteOneUnit(Long unitId);

    public List<Personel> getPersonelsByUnitId(Long unitId);
}

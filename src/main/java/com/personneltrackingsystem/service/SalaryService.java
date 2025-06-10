package com.personneltrackingsystem.service;

import java.time.YearMonth;
import java.util.List;

import com.personneltrackingsystem.dto.DtoSalary;
import com.personneltrackingsystem.dto.DtoSalaryCalculationRequest;
import com.personneltrackingsystem.entity.Personel;

public interface SalaryService {
    
    DtoSalary calculateSalaryForPersonel(Personel personel, YearMonth month);
    
    List<DtoSalary> calculateAllSalariesForMonth(YearMonth month);
    
    List<DtoSalary> getSalaryHistoryForPersonel(Long personelId);
    
    DtoSalary getSalaryForPersonelAndMonth(Long personelId, YearMonth month);
    
    List<DtoSalary> getAllSalariesForMonth(YearMonth month);
    
    List<DtoSalary> calculateSalaries(DtoSalaryCalculationRequest request);
} 
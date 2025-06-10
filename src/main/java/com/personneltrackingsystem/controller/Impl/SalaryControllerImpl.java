package com.personneltrackingsystem.controller.impl;

import com.personneltrackingsystem.controller.SalaryController;
import com.personneltrackingsystem.dto.DtoSalary;
import com.personneltrackingsystem.dto.DtoSalaryCalculationRequest;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.service.SalaryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SalaryControllerImpl implements SalaryController {

    private final SalaryService salaryService;

    @Override
    public ResponseEntity<List<DtoSalary>> getSalaryHistoryForPersonel(Long personelId) {
        List<DtoSalary> salaries = salaryService.getSalaryHistoryForPersonel(personelId);
        return ResponseEntity.ok(salaries);
    }

    @Override
    public ResponseEntity<DtoSalary> getSalaryForPersonelAndMonth(Long personelId, String yearMonth) {
        try {
            YearMonth month = YearMonth.parse(yearMonth);
            DtoSalary salary = salaryService.getSalaryForPersonelAndMonth(personelId, month);
            return ResponseEntity.ok(salary);
        } catch (DateTimeParseException e) {
            throw new ValidationException(MessageType.INVALID_DATE_FORMAT);
        }
    }

    @Override
    public ResponseEntity<List<DtoSalary>> getAllSalariesForMonth(String yearMonth) {
        try {
            YearMonth month = YearMonth.parse(yearMonth);
            List<DtoSalary> salaries = salaryService.getAllSalariesForMonth(month);
            return ResponseEntity.ok(salaries);
        } catch (DateTimeParseException e) {
            throw new ValidationException(MessageType.INVALID_DATE_FORMAT);
        }
    }

    @Override
    public ResponseEntity<List<DtoSalary>> calculateSalaries(DtoSalaryCalculationRequest request) {
        List<DtoSalary> salaries = salaryService.calculateSalaries(request);
        return ResponseEntity.ok(salaries);
    }
} 
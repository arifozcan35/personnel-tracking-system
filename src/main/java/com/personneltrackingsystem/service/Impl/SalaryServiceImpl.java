package com.personneltrackingsystem.service.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personneltrackingsystem.dto.DtoSalary;
import com.personneltrackingsystem.dto.DtoSalaryCalculationRequest;
import com.personneltrackingsystem.entity.OperationType;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Salary;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.mapper.SalaryMapper;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.repository.SalaryRepository;
import com.personneltrackingsystem.repository.TurnstileRegistrationLogRepository;
import com.personneltrackingsystem.service.PersonelService;
import com.personneltrackingsystem.service.SalaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final PersonelRepository personelRepository;
    private final TurnstileRegistrationLogRepository turnstileLogRepository;
    private final PersonelService personelService;
    private final SalaryMapper salaryMapper;
    
    @Value("${app.salary.late-penalty-amount:300.0}")
    private Double latePenaltyAmount;
    
    @Value("${app.salary.late-threshold-minutes:555}")
    private Integer lateThresholdMinutes; // 09:15 = 9*60+15 = 555 minutes
    

    @Scheduled(cron = "0 5 0 1 * *")
    public void scheduledSalaryCalculation() {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        log.info("Salary calculation process started: {}", previousMonth);
        calculateAllSalariesForMonth(previousMonth);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DtoSalary calculateSalaryForPersonel(Personel personel, YearMonth month) {
        Optional<Salary> existingSalary = salaryRepository.findByPersonelIdAndSalaryMonth(personel, month);
        if (existingSalary.isPresent()) {
            return salaryMapper.salaryToDtoSalary(existingSalary.get());
        }

        if (ObjectUtils.isEmpty(personel.getPersonelTypeId()) || ObjectUtils.isEmpty(personel.getPersonelTypeId().getBaseSalary())) {
            throw new BaseException(new ErrorMessage(MessageType.PERSONEL_TYPE_OR_SALARY_NOT_FOUND, personel.getPersonelId().toString()));
        }

        Double baseSalary = personel.getPersonelTypeId().getBaseSalary();

        Integer lateDays = turnstileLogRepository.countLateDaysInMonth(
            personel.getPersonelId(), 
            OperationType.IN.name(), 
            lateThresholdMinutes, 
            month.getYear(), 
            month.getMonthValue()
        );
        
        if (ObjectUtils.isEmpty(lateDays)) {
            lateDays = 0;
        }

        Double totalPenalty = lateDays * latePenaltyAmount;

        Double finalSalary = baseSalary - totalPenalty;
        if (finalSalary < 0) {
            finalSalary = 0.0;
        }
        
        DtoSalary dtoSalary = new DtoSalary();
        dtoSalary.setPersonelId(personel.getPersonelId());
        dtoSalary.setPersonelName(personel.getName());
        dtoSalary.setPersonelEmail(personel.getEmail());
        dtoSalary.setPersonelTypeName(personel.getPersonelTypeId().getPersonelTypeName());
        dtoSalary.setSalaryMonth(month);
        dtoSalary.setBaseAmount(baseSalary);
        dtoSalary.setLateDays(lateDays);
        dtoSalary.setLatePenaltyAmount(totalPenalty);
        dtoSalary.setFinalAmount(finalSalary);
        dtoSalary.setCalculationDate(LocalDate.now());
        dtoSalary.setIsPaid(false);
        
        return dtoSalary;
    }
    
    @Override
    @Transactional
    public List<DtoSalary> calculateAllSalariesForMonth(YearMonth month) {
        log.info("Salary calculation process started for all personnel: {}", month);
        
        List<Personel> allPersonel = personelRepository.findAll();
        List<DtoSalary> result = new ArrayList<>();
        
        for (Personel personel : allPersonel) {
            try {
                DtoSalary calculated = calculateSalaryForPersonel(personel, month);
                
                Salary salary = new Salary();
                salary.setPersonelId(personel);
                salary.setSalaryMonth(month);
                salary.setBaseAmount(calculated.getBaseAmount());
                salary.setLateDays(calculated.getLateDays());
                salary.setLatePenaltyAmount(calculated.getLatePenaltyAmount());
                salary.setFinalAmount(calculated.getFinalAmount());
                salary.setCalculationDate(LocalDate.now());
                salary.setIsPaid(false);
                
                Salary savedSalary = salaryRepository.save(salary);
                result.add(salaryMapper.salaryToDtoSalary(savedSalary));
                
                log.info("Salary calculated: Personnel={}, Month={}, Amount={}, Late Days={}", 
                        personel.getPersonelId(), month, calculated.getFinalAmount(), calculated.getLateDays());
            } catch (Exception e) {
                log.error("Salary calculation error: Personnel={}, Month={}, Error={}", 
                        personel.getPersonelId(), month, e.getMessage(), e);
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DtoSalary> getSalaryHistoryForPersonel(Long personelId) {
        Personel personel = personelService.checkIfPersonelExists(personelId);
        List<Salary> salaries = salaryRepository.findByPersonelId(personel);
        return salaryMapper.salaryListToDtoSalaryList(salaries);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DtoSalary getSalaryForPersonelAndMonth(Long personelId, YearMonth month) {
        Personel personel = personelService.checkIfPersonelExists(personelId);
        Salary salary = salaryRepository.findByPersonelIdAndSalaryMonth(personel, month)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.SALARY_NOT_FOUND, 
                        personelId + " - " + month.toString())));
        
        return salaryMapper.salaryToDtoSalary(salary);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DtoSalary> getAllSalariesForMonth(YearMonth month) {
        List<Salary> salaries = salaryRepository.findBySalaryMonth(month);
        return salaryMapper.salaryListToDtoSalaryList(salaries);
    }
    
    @Override
    @Transactional
    public List<DtoSalary> calculateSalaries(DtoSalaryCalculationRequest request) {
        YearMonth month = request.getYearMonth();
        
        if (ObjectUtils.isEmpty(month)) {
            throw new BaseException(new ErrorMessage(MessageType.MONTH_REQUIRED, "Year and month information is required"));
        }

        if (ObjectUtils.isNotEmpty(request.getPersonelId())) {
            Personel personel = personelService.checkIfPersonelExists(request.getPersonelId());

            if (Boolean.TRUE.equals(request.getForceRecalculation()) || 
                !salaryRepository.findByPersonelIdAndSalaryMonth(personel, month).isPresent()) {
                
                DtoSalary calculated = calculateSalaryForPersonel(personel, month);

                salaryRepository.findByPersonelIdAndSalaryMonth(personel, month)
                    .ifPresent(salaryRepository::delete);

                Salary salary = new Salary();
                salary.setPersonelId(personel);
                salary.setSalaryMonth(month);
                salary.setBaseAmount(calculated.getBaseAmount());
                salary.setLateDays(calculated.getLateDays());
                salary.setLatePenaltyAmount(calculated.getLatePenaltyAmount());
                salary.setFinalAmount(calculated.getFinalAmount());
                salary.setCalculationDate(LocalDate.now());
                salary.setIsPaid(false);
                
                Salary savedSalary = salaryRepository.save(salary);
                return List.of(salaryMapper.salaryToDtoSalary(savedSalary));
            } else {
                Salary existingSalary = salaryRepository.findByPersonelIdAndSalaryMonth(personel, month)
                        .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.SALARY_NOT_FOUND, 
                                personel.getPersonelId() + " - " + month.toString())));
                return List.of(salaryMapper.salaryToDtoSalary(existingSalary));
            }
        } else {
            if (Boolean.TRUE.equals(request.getForceRecalculation())) {
                List<Salary> existingSalaries = salaryRepository.findBySalaryMonth(month);

                salaryRepository.deleteAll(existingSalaries);
            }

            return calculateAllSalariesForMonth(month);
        }
    }
} 
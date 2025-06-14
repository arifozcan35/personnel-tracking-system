package com.personneltrackingsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.personneltrackingsystem.dto.DtoSalary;
import com.personneltrackingsystem.dto.DtoSalaryCalculationRequest;
import com.personneltrackingsystem.entity.OperationType;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.PersonelType;
import com.personneltrackingsystem.entity.Salary;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.mapper.SalaryMapper;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.repository.SalaryRepository;
import com.personneltrackingsystem.repository.TurnstileRegistrationLogRepository;
import com.personneltrackingsystem.service.PersonelService;

@ExtendWith(MockitoExtension.class)
public class SalaryServiceImplTest {

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private PersonelRepository personelRepository;

    @Mock
    private TurnstileRegistrationLogRepository turnstileLogRepository;

    @Mock
    private PersonelService personelService;

    @Mock
    private SalaryMapper salaryMapper;

    @InjectMocks
    private SalaryServiceImpl salaryService;

    private Personel testPersonel;
    private PersonelType testPersonelType;
    private Salary testSalary;
    private DtoSalary testDtoSalary;
    private YearMonth testMonth;

    @BeforeEach
    void setUp() {
        testMonth = YearMonth.of(2025, 6);
        
        testPersonelType = new PersonelType();
        testPersonelType.setPersonelTypeId(1L);
        testPersonelType.setPersonelTypeName("Test Personel Tipi");
        testPersonelType.setBaseSalary(50000.0);
        
        testPersonel = new Personel();
        testPersonel.setPersonelId(1L);
        testPersonel.setName("Test Personel");
        testPersonel.setEmail("test@test.com");
        testPersonel.setPersonelTypeId(testPersonelType);
        
        testSalary = new Salary();
        testSalary.setSalaryId(1L);
        testSalary.setPersonelId(testPersonel);
        testSalary.setSalaryMonth(testMonth);
        testSalary.setBaseAmount(50000.0);
        testSalary.setLateDays(2);
        testSalary.setLatePenaltyAmount(600.0);
        testSalary.setFinalAmount(49400.0);
        testSalary.setCalculationDate(LocalDate.now());
        testSalary.setIsPaid(false);
        
        testDtoSalary = new DtoSalary();
        testDtoSalary.setSalaryId(1L);
        testDtoSalary.setPersonelId(1L);
        testDtoSalary.setPersonelName("Test Personel");
        testDtoSalary.setPersonelEmail("test@test.com");
        testDtoSalary.setPersonelTypeName("Test Personel Tipi");
        testDtoSalary.setSalaryMonth(testMonth);
        testDtoSalary.setBaseAmount(50000.0);
        testDtoSalary.setLateDays(2);
        testDtoSalary.setLatePenaltyAmount(600.0);
        testDtoSalary.setFinalAmount(49400.0);
        testDtoSalary.setCalculationDate(LocalDate.now());
        testDtoSalary.setIsPaid(false);
        
        ReflectionTestUtils.setField(salaryService, "latePenaltyAmount", 300.0);
        ReflectionTestUtils.setField(salaryService, "lateThresholdMinutes", 555);
    }

    @Test
    @DisplayName("Var olan maaş kaydını getirme testi")
    void calculateSalaryForPersonel_ExistingSalary_ReturnsExistingSalary() {
        // Arrange
        when(salaryRepository.findByPersonelIdAndSalaryMonth(testPersonel, testMonth))
                .thenReturn(Optional.of(testSalary));
        when(salaryMapper.salaryToDtoSalary(testSalary)).thenReturn(testDtoSalary);

        // Act
        DtoSalary result = salaryService.calculateSalaryForPersonel(testPersonel, testMonth);

        // Assert
        assertNotNull(result);
        assertEquals(testDtoSalary, result);
        verify(salaryRepository).findByPersonelIdAndSalaryMonth(testPersonel, testMonth);
        verify(salaryMapper).salaryToDtoSalary(testSalary);
    }

    @Test
    @DisplayName("Yeni maaş hesaplama testi")
    void calculateSalaryForPersonel_NewSalary_CalculatesCorrectly() {
        // Arrange
        when(salaryRepository.findByPersonelIdAndSalaryMonth(testPersonel, testMonth))
                .thenReturn(Optional.empty());
        when(turnstileLogRepository.countLateDaysInMonth(
                anyLong(), anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(2);

        // Act
        DtoSalary result = salaryService.calculateSalaryForPersonel(testPersonel, testMonth);

        // Assert
        assertNotNull(result);
        assertEquals(testPersonel.getPersonelId(), result.getPersonelId());
        assertEquals(testPersonel.getName(), result.getPersonelName());
        assertEquals(testPersonel.getEmail(), result.getPersonelEmail());
        assertEquals(testPersonel.getPersonelTypeId().getPersonelTypeName(), result.getPersonelTypeName());
        assertEquals(testMonth, result.getSalaryMonth());
        assertEquals(50000.0, result.getBaseAmount());
        assertEquals(2, result.getLateDays());
        assertEquals(600.0, result.getLatePenaltyAmount());
        assertEquals(49400.0, result.getFinalAmount());
        assertFalse(result.getIsPaid());
        
        verify(salaryRepository).findByPersonelIdAndSalaryMonth(testPersonel, testMonth);
        verify(turnstileLogRepository).countLateDaysInMonth(
                eq(testPersonel.getPersonelId()), 
                eq(OperationType.IN.name()), 
                eq(555), 
                eq(testMonth.getYear()), 
                eq(testMonth.getMonthValue()));
    }

    @Test
    @DisplayName("Tüm personel için maaş hesaplama testi")
    void calculateAllSalariesForMonth_CalculatesAllSalaries() {
        // Arrange
        List<Personel> personelList = new ArrayList<>();
        personelList.add(testPersonel);
        
        when(personelRepository.findAll()).thenReturn(personelList);
        when(salaryRepository.findByPersonelIdAndSalaryMonth(testPersonel, testMonth))
                .thenReturn(Optional.empty());
        when(turnstileLogRepository.countLateDaysInMonth(
                anyLong(), anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(2);
        when(salaryRepository.save(any(Salary.class))).thenReturn(testSalary);
        when(salaryMapper.salaryToDtoSalary(testSalary)).thenReturn(testDtoSalary);

        // Act
        List<DtoSalary> results = salaryService.calculateAllSalariesForMonth(testMonth);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testDtoSalary, results.get(0));
        
        verify(personelRepository).findAll();
        verify(salaryRepository).save(any(Salary.class));
        verify(salaryMapper).salaryToDtoSalary(testSalary);
    }

    @Test
    @DisplayName("Personel maaş geçmişini getirme testi")
    void getSalaryHistoryForPersonel_ReturnsPersonelHistory() {
        // Arrange
        List<Salary> salaries = new ArrayList<>();
        salaries.add(testSalary);
        List<DtoSalary> dtoSalaries = new ArrayList<>();
        dtoSalaries.add(testDtoSalary);
        
        when(personelService.checkIfPersonelExists(testPersonel.getPersonelId())).thenReturn(testPersonel);
        when(salaryRepository.findByPersonelId(testPersonel)).thenReturn(salaries);
        when(salaryMapper.salaryListToDtoSalaryList(salaries)).thenReturn(dtoSalaries);

        // Act
        List<DtoSalary> results = salaryService.getSalaryHistoryForPersonel(testPersonel.getPersonelId());

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testDtoSalary, results.get(0));
        
        verify(personelService).checkIfPersonelExists(testPersonel.getPersonelId());
        verify(salaryRepository).findByPersonelId(testPersonel);
        verify(salaryMapper).salaryListToDtoSalaryList(salaries);
    }

    @Test
    @DisplayName("Belirli bir ay için personel maaşını getirme testi")
    void getSalaryForPersonelAndMonth_ReturnsSalary() {
        // Arrange
        when(personelService.checkIfPersonelExists(testPersonel.getPersonelId())).thenReturn(testPersonel);
        when(salaryRepository.findByPersonelIdAndSalaryMonth(testPersonel, testMonth))
                .thenReturn(Optional.of(testSalary));
        when(salaryMapper.salaryToDtoSalary(testSalary)).thenReturn(testDtoSalary);

        // Act
        DtoSalary result = salaryService.getSalaryForPersonelAndMonth(testPersonel.getPersonelId(), testMonth);

        // Assert
        assertNotNull(result);
        assertEquals(testDtoSalary, result);
        
        verify(personelService).checkIfPersonelExists(testPersonel.getPersonelId());
        verify(salaryRepository).findByPersonelIdAndSalaryMonth(testPersonel, testMonth);
        verify(salaryMapper).salaryToDtoSalary(testSalary);
    }

    @Test
    @DisplayName("Belirli bir ay için personel maaşını bulamazsa hata fırlatma testi")
    void getSalaryForPersonelAndMonth_ThrowsExceptionWhenNotFound() {
        // Arrange
        when(personelService.checkIfPersonelExists(testPersonel.getPersonelId())).thenReturn(testPersonel);
        when(salaryRepository.findByPersonelIdAndSalaryMonth(testPersonel, testMonth))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, 
                () -> salaryService.getSalaryForPersonelAndMonth(testPersonel.getPersonelId(), testMonth));
        
        verify(personelService).checkIfPersonelExists(testPersonel.getPersonelId());
        verify(salaryRepository).findByPersonelIdAndSalaryMonth(testPersonel, testMonth);
    }

    @Test
    @DisplayName("Belirli bir ay için tüm maaşları getirme testi")
    void getAllSalariesForMonth_ReturnsAllSalaries() {
        // Arrange
        List<Salary> salaries = new ArrayList<>();
        salaries.add(testSalary);
        List<DtoSalary> dtoSalaries = new ArrayList<>();
        dtoSalaries.add(testDtoSalary);
        
        when(salaryRepository.findBySalaryMonth(testMonth)).thenReturn(salaries);
        when(salaryMapper.salaryListToDtoSalaryList(salaries)).thenReturn(dtoSalaries);

        // Act
        List<DtoSalary> results = salaryService.getAllSalariesForMonth(testMonth);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testDtoSalary, results.get(0));
        
        verify(salaryRepository).findBySalaryMonth(testMonth);
        verify(salaryMapper).salaryListToDtoSalaryList(salaries);
    }

    @Test
    @DisplayName("Belirli bir personel için maaş hesaplama isteği testi")
    void calculateSalaries_ForSpecificPersonel_CalculatesAndSaves() {
        // Arrange
        DtoSalaryCalculationRequest request = new DtoSalaryCalculationRequest();
        request.setYear(2025);
        request.setMonthNumber(6);
        request.setPersonelId(1L);
        request.setForceRecalculation(true);
        
        when(personelService.checkIfPersonelExists(testPersonel.getPersonelId())).thenReturn(testPersonel);
        when(salaryRepository.findByPersonelIdAndSalaryMonth(testPersonel, testMonth))
                .thenReturn(Optional.of(testSalary));
        org.mockito.Mockito.doNothing().when(salaryRepository).delete(testSalary);
        when(salaryRepository.save(any(Salary.class))).thenReturn(testSalary);
        when(salaryMapper.salaryToDtoSalary(testSalary)).thenReturn(testDtoSalary);

        // Act
        List<DtoSalary> results = salaryService.calculateSalaries(request);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testDtoSalary, results.get(0));
        
        verify(personelService).checkIfPersonelExists(testPersonel.getPersonelId());
        verify(salaryRepository).delete(testSalary);
        verify(salaryRepository).save(any(Salary.class));
        verify(salaryMapper, times(2)).salaryToDtoSalary(testSalary);
    }

    @Test
    @DisplayName("Tüm personel için maaş hesaplama isteği testi")
    void calculateSalaries_ForAllPersonel_CalculatesAndSavesAll() {
        // Arrange
        DtoSalaryCalculationRequest request = new DtoSalaryCalculationRequest();
        request.setYear(2025);
        request.setMonthNumber(6);
        request.setForceRecalculation(true);
        
        List<Salary> existingSalaries = new ArrayList<>();
        existingSalaries.add(testSalary);
        
        List<Personel> personelList = new ArrayList<>();
        personelList.add(testPersonel);
        
        when(salaryRepository.findBySalaryMonth(testMonth)).thenReturn(existingSalaries);
        when(personelRepository.findAll()).thenReturn(personelList);
        when(salaryRepository.findByPersonelIdAndSalaryMonth(testPersonel, testMonth))
                .thenReturn(Optional.empty());
        when(turnstileLogRepository.countLateDaysInMonth(
                anyLong(), anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(2);
        when(salaryRepository.save(any(Salary.class))).thenReturn(testSalary);
        when(salaryMapper.salaryToDtoSalary(testSalary)).thenReturn(testDtoSalary);

        // Act
        List<DtoSalary> results = salaryService.calculateSalaries(request);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testDtoSalary, results.get(0));
        
        verify(salaryRepository).findBySalaryMonth(testMonth);
        verify(salaryRepository).deleteAll(existingSalaries);
        verify(personelRepository).findAll();
        verify(salaryRepository).save(any(Salary.class));
        verify(salaryMapper).salaryToDtoSalary(testSalary);
    }

    @Test
    @DisplayName("Zamanlı maaş hesaplama testi")
    void scheduledSalaryCalculation_CalculatesForPreviousMonth() {
        // Bu metodun çalışmasını test etmek için stub bir test oluşturuyoruz
        // Normalde @Scheduled annotasyonu ile çalışan metotlar doğrudan test edilemez
        
        // Mockları ayarla
        List<Personel> personelList = new ArrayList<>();
        personelList.add(testPersonel);
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        
        when(personelRepository.findAll()).thenReturn(personelList);
        when(salaryRepository.findByPersonelIdAndSalaryMonth(any(), any()))
                .thenReturn(Optional.empty());
        when(turnstileLogRepository.countLateDaysInMonth(
                anyLong(), anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(2);
        when(salaryRepository.save(any(Salary.class))).thenReturn(testSalary);
        when(salaryMapper.salaryToDtoSalary(any())).thenReturn(testDtoSalary);

        // Act
        salaryService.scheduledSalaryCalculation();

        // Assert - En azından metot hata vermeden çalışabilmeli
        verify(personelRepository).findAll();
        // previousMonth değişkeninin dinamik olması nedeniyle tam eşleşme yerine any() kullanıyoruz
        verify(salaryRepository, times(personelList.size())).save(any(Salary.class));
    }
} 
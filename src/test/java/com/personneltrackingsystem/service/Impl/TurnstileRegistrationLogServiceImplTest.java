package com.personneltrackingsystem.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;
import com.personneltrackingsystem.dto.DtoTurnstilePassageFullRequest;
import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.entity.OperationType;
import com.personneltrackingsystem.entity.TurnstileRegistrationLog;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.TurnstileRegistrationLogMapper;
import com.personneltrackingsystem.repository.TurnstileRegistrationLogRepository;
import com.personneltrackingsystem.service.cache.HazelcastCacheService;
import com.personneltrackingsystem.service.cache.RedisCacheService;
import com.personneltrackingsystem.service.impl.TurnstileRegistrationLogServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TurnstileRegistrationLogServiceImplTest {

    @Mock
    private TurnstileRegistrationLogRepository turnstileRegistrationLogRepository;

    @Mock
    private TurnstileRegistrationLogMapper turnstileRegistrationLogMapper;

    @Mock
    private HazelcastCacheService hazelcastCacheService;

    @Mock
    private RedisCacheService redisCacheService;

    @InjectMocks
    private TurnstileRegistrationLogServiceImpl turnstileRegistrationLogService;

    @Captor
    private ArgumentCaptor<TurnstileRegistrationLog> logCaptor;

    private DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU;
    private TurnstileRegistrationLog turnstileRegistrationLog;
    private YearMonth yearMonth;

    @BeforeEach
    void setUp() {
        dtoTurnstileRegistrationLogIU = new DtoTurnstileRegistrationLogIU();
        dtoTurnstileRegistrationLogIU.setPersonelId(1L);
        dtoTurnstileRegistrationLogIU.setTurnstileId(2L);
        dtoTurnstileRegistrationLogIU.setOperationTime(LocalDateTime.now());
        dtoTurnstileRegistrationLogIU.setOperationType(OperationType.IN);

        turnstileRegistrationLog = new TurnstileRegistrationLog();
        
        yearMonth = YearMonth.now();
    }

    @Test
    void testSaveOneTurnstileRegistrationLog() {
        // Arrange
        when(turnstileRegistrationLogMapper.dtoTurnstileRegistrationLogIUToTurnstileRegistrationLog(dtoTurnstileRegistrationLogIU))
                .thenReturn(turnstileRegistrationLog);

        // Act
        turnstileRegistrationLogService.saveOneTurnstileRegistrationLog(dtoTurnstileRegistrationLogIU);

        // Assert
        verify(turnstileRegistrationLogRepository).save(turnstileRegistrationLog);
    }

    @Test
    void testGetMonthlyTurnstileBasedPersonnelListFromHazelcast_WhenDataExists() {
        // Arrange
        HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> expectedData = new HashMap<>();
        when(hazelcastCacheService.getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth))
                .thenReturn(Optional.of(expectedData));

        // Act
        HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> result = 
                turnstileRegistrationLogService.getMonthlyTurnstileBasedPersonnelListFromHazelcast(yearMonth);

        // Assert
        assertEquals(expectedData, result);
        verify(hazelcastCacheService).getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth);
    }

    @Test
    void testGetMonthlyTurnstileBasedPersonnelListFromHazelcast_WhenDataNotExists() {
        // Arrange
        when(hazelcastCacheService.getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth))
                .thenReturn(Optional.empty());

        // Act
        HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> result = 
                turnstileRegistrationLogService.getMonthlyTurnstileBasedPersonnelListFromHazelcast(yearMonth);

        // Assert
        assertTrue(result.isEmpty());
        verify(hazelcastCacheService).getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth);
    }

    @Test
    void testGetMonthlyTurnstileBasedPersonnelListFromRedis_WhenDataExists() {
        // Arrange
        HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> expectedData = new HashMap<>();
        when(redisCacheService.getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth))
                .thenReturn(Optional.of(expectedData));

        // Act
        HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> result = 
                turnstileRegistrationLogService.getMonthlyTurnstileBasedPersonnelListFromRedis(yearMonth);

        // Assert
        assertEquals(expectedData, result);
        verify(redisCacheService).getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth);
    }

    @Test
    void testGetMonthlyTurnstileBasedPersonnelListFromRedis_WhenDataNotExists() {
        // Arrange
        when(redisCacheService.getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth))
                .thenReturn(Optional.empty());

        // Act
        HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> result = 
                turnstileRegistrationLogService.getMonthlyTurnstileBasedPersonnelListFromRedis(yearMonth);

        // Assert
        assertTrue(result.isEmpty());
        verify(redisCacheService).getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth);
    }

    @Test
    void testValidateAndGetYearMonth_WhenYearMonthIsProvided() {
        // Act
        YearMonth result = turnstileRegistrationLogService.validateAndGetYearMonth(yearMonth);

        // Assert
        assertEquals(yearMonth, result);
    }

    @Test
    void testValidateAndGetYearMonth_WhenYearMonthIsNull() {
        // Act
        YearMonth result = turnstileRegistrationLogService.validateAndGetYearMonth(null);

        // Assert
        assertEquals(YearMonth.now(), result);
    }

    @Test
    void testValidateTurnstilePassage_FirstTimeEntry() {
        // Arrange
        DtoTurnstilePassageFullRequest request = new DtoTurnstilePassageFullRequest();
        request.setPersonelId(1L);
        request.setWantedToEnterTurnstileId(2L);
        request.setOperationType(OperationType.IN);

        when(turnstileRegistrationLogRepository.findOperationTypesByPersonelAndTurnstile(1L, 2L))
                .thenReturn(new ArrayList<>());

        // Act
        turnstileRegistrationLogService.validateTurnstilePassage(request);

        // Assert - No exception is thrown
        verify(turnstileRegistrationLogRepository).findOperationTypesByPersonelAndTurnstile(1L, 2L);
    }

    @Test
    void testValidateTurnstilePassage_FirstTimeExit_ShouldThrowException() {
        // Arrange
        DtoTurnstilePassageFullRequest request = new DtoTurnstilePassageFullRequest();
        request.setPersonelId(1L);
        request.setWantedToEnterTurnstileId(2L);
        request.setOperationType(OperationType.OUT);

        when(turnstileRegistrationLogRepository.findOperationTypesByPersonelAndTurnstile(1L, 2L))
                .thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(ValidationException.class, () -> 
            turnstileRegistrationLogService.validateTurnstilePassage(request)
        );
        verify(turnstileRegistrationLogRepository).findOperationTypesByPersonelAndTurnstile(1L, 2L);
    }

    @Test
    void testValidateTurnstilePassage_AfterEntry_ValidExit() {
        // Arrange
        DtoTurnstilePassageFullRequest request = new DtoTurnstilePassageFullRequest();
        request.setPersonelId(1L);
        request.setWantedToEnterTurnstileId(2L);
        request.setOperationType(OperationType.OUT);

        List<String> operations = new ArrayList<>();
        operations.add("IN");
        
        when(turnstileRegistrationLogRepository.findOperationTypesByPersonelAndTurnstile(1L, 2L))
                .thenReturn(operations);

        // Act
        turnstileRegistrationLogService.validateTurnstilePassage(request);

        // Assert - No exception is thrown
        verify(turnstileRegistrationLogRepository).findOperationTypesByPersonelAndTurnstile(1L, 2L);
    }

    @Test
    void testValidateTurnstilePassage_AfterEntry_InvalidEntry_ShouldThrowException() {
        // Arrange
        DtoTurnstilePassageFullRequest request = new DtoTurnstilePassageFullRequest();
        request.setPersonelId(1L);
        request.setWantedToEnterTurnstileId(2L);
        request.setOperationType(OperationType.IN);

        List<String> operations = new ArrayList<>();
        operations.add("IN");
        
        when(turnstileRegistrationLogRepository.findOperationTypesByPersonelAndTurnstile(1L, 2L))
                .thenReturn(operations);

        // Act & Assert
        assertThrows(ValidationException.class, () -> 
            turnstileRegistrationLogService.validateTurnstilePassage(request)
        );
        verify(turnstileRegistrationLogRepository).findOperationTypesByPersonelAndTurnstile(1L, 2L);
    }

    @Test
    void testValidateTurnstilePassage_AfterExit_ValidEntry() {
        // Arrange
        DtoTurnstilePassageFullRequest request = new DtoTurnstilePassageFullRequest();
        request.setPersonelId(1L);
        request.setWantedToEnterTurnstileId(2L);
        request.setOperationType(OperationType.IN);

        List<String> operations = new ArrayList<>();
        operations.add("OUT");
        
        when(turnstileRegistrationLogRepository.findOperationTypesByPersonelAndTurnstile(1L, 2L))
                .thenReturn(operations);

        // Act
        turnstileRegistrationLogService.validateTurnstilePassage(request);

        // Assert - No exception is thrown
        verify(turnstileRegistrationLogRepository).findOperationTypesByPersonelAndTurnstile(1L, 2L);
    }

    @Test
    void testValidateTurnstilePassage_AfterExit_InvalidExit_ShouldThrowException() {
        // Arrange
        DtoTurnstilePassageFullRequest request = new DtoTurnstilePassageFullRequest();
        request.setPersonelId(1L);
        request.setWantedToEnterTurnstileId(2L);
        request.setOperationType(OperationType.OUT);

        List<String> operations = new ArrayList<>();
        operations.add("OUT");
        
        when(turnstileRegistrationLogRepository.findOperationTypesByPersonelAndTurnstile(1L, 2L))
                .thenReturn(operations);

        // Act & Assert
        assertThrows(ValidationException.class, () -> 
            turnstileRegistrationLogService.validateTurnstilePassage(request)
        );
        verify(turnstileRegistrationLogRepository).findOperationTypesByPersonelAndTurnstile(1L, 2L);
    }

    @Test
    void testDailyCacheRefresh() {
        // Arrange
        Map<String, List<DtoTurnstileBasedPersonnelEntry>> dailyRecords = new HashMap<>();
        Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> allDailyRecords = new HashMap<>();
        allDailyRecords.put("2023-01-01", dailyRecords);
        
        when(redisCacheService.getAllDailyTurnstilePassageRecords()).thenReturn(allDailyRecords);
        
        // Act
        turnstileRegistrationLogService.dailyCacheRefresh();
        
        // Assert
        verify(redisCacheService).getAllDailyTurnstilePassageRecords();
        verify(hazelcastCacheService).addDailyRecordsToMonthlyMap(any(LocalDate.class), eq(dailyRecords));
        verify(redisCacheService).transferDailyRecordsToMonthlyMap();
    }
    
    @Test
    void testDailyCacheRefresh_EmptyRecords() {
        // Arrange
        Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> emptyRecords = new HashMap<>();
        when(redisCacheService.getAllDailyTurnstilePassageRecords()).thenReturn(emptyRecords);
        
        // Act
        turnstileRegistrationLogService.dailyCacheRefresh();
        
        // Assert
        verify(redisCacheService).getAllDailyTurnstilePassageRecords();
        verify(hazelcastCacheService, never()).addDailyRecordsToMonthlyMap(any(), any());
        verify(redisCacheService).transferDailyRecordsToMonthlyMap();
    }
    
    @Test
    void testDailyCacheRefresh_ExceptionHandling() {
        // Arrange
        when(redisCacheService.getAllDailyTurnstilePassageRecords()).thenThrow(new RuntimeException("Test exception"));
        
        // Act
        turnstileRegistrationLogService.dailyCacheRefresh();
        
        // Assert
        verify(redisCacheService).getAllDailyTurnstilePassageRecords();
        verify(hazelcastCacheService, never()).addDailyRecordsToMonthlyMap(any(), any());
        verify(redisCacheService, never()).transferDailyRecordsToMonthlyMap();
    }
} 
package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.*;
import com.personneltrackingsystem.entity.*;
import com.personneltrackingsystem.dto.event.TurnstilePassageEvent;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.service.cache.RedisCacheService;
import com.personneltrackingsystem.service.kafka.KafkaProducerService;
import com.personneltrackingsystem.mapper.TurnstileMapper;
import com.personneltrackingsystem.repository.TurnstileRepository;
import com.personneltrackingsystem.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TurnstileServiceImplTest {

    @Mock
    private TurnstileRepository turnstileRepository;

    @Mock
    private TurnstileRegistrationLogService turnstileRegistrationLogService;

    @Mock
    private GateService gateService;

    @Mock
    private PersonelService personelService;

    @Mock
    private TurnstileMapper turnstileMapper;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private RedisCacheService redisCacheService;

    @InjectMocks
    private TurnstileServiceImpl turnstileService;

    private Turnstile turnstile;
    private DtoTurnstile dtoTurnstile;
    private DtoTurnstileIU dtoTurnstileIU;
    private Personel personel;
    private Gate gate;
    private Unit unit;

    @BeforeEach
    void setUp() {
        // Turnstile test verileri
        turnstile = new Turnstile();
        turnstile.setTurnstileId(1L);
        turnstile.setTurnstileName("Test Turnstile");

        dtoTurnstile = new DtoTurnstile();
        dtoTurnstile.setTurnstileName("Test Turnstile");

        dtoTurnstileIU = new DtoTurnstileIU();
        dtoTurnstileIU.setTurnstileName("Test Turnstile");

        // Personel test verileri
        personel = new Personel();
        personel.setPersonelId(1L);
        personel.setName("Test Personel");
        personel.setEmail("test@example.com");

        // Gate test verileri
        gate = new Gate();
        gate.setGateId(1L);
        gate.setGateName("Test Gate");
        gate.setMainEntrance(true);

        // Unit test verileri
        unit = new Unit();
        unit.setUnitId(1L);
        unit.setUnitName("Test Unit");
        
        Personel adminPersonel = new Personel();
        adminPersonel.setPersonelId(2L);
        adminPersonel.setName("Admin Personel");
        adminPersonel.setEmail("admin@example.com");
        
        unit.setAdministratorPersonelId(adminPersonel);
        gate.setUnitId(unit);
    }

    @Test
    void getAllTurnstiles_ShouldReturnAllTurnstiles() {
        // Arrange
        List<Turnstile> turnstileList = Collections.singletonList(turnstile);
        List<DtoTurnstile> dtoTurnstileList = Collections.singletonList(dtoTurnstile);
        
        when(turnstileRepository.findAll()).thenReturn(turnstileList);
        when(turnstileMapper.turnstileListToDtoTurnstileList(turnstileList)).thenReturn(dtoTurnstileList);

        // Act
        List<DtoTurnstile> result = turnstileService.getAllTurnstiles();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dtoTurnstile.getTurnstileName(), result.get(0).getTurnstileName());
        
        verify(turnstileRepository).findAll();
        verify(turnstileMapper).turnstileListToDtoTurnstileList(turnstileList);
    }

    @Test
    void getTurnstileById_WhenTurnstileExists_ShouldReturnTurnstile() {
        // Arrange
        when(turnstileRepository.findById(1L)).thenReturn(Optional.of(turnstile));
        when(turnstileMapper.turnstileToDtoTurnstile(turnstile)).thenReturn(dtoTurnstile);

        // Act
        Optional<DtoTurnstile> result = turnstileService.getTurnstileById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(dtoTurnstile.getTurnstileName(), result.get().getTurnstileName());
        
        verify(turnstileRepository).findById(1L);
        verify(turnstileMapper).turnstileToDtoTurnstile(turnstile);
    }

    @Test
    void getTurnstileById_WhenTurnstileNotExists_ShouldThrowException() {
        // Arrange
        when(turnstileRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> turnstileService.getTurnstileById(1L));
        verify(turnstileRepository).findById(1L);
    }

    @Test
    void getOneTurnstile_WhenTurnstileExists_ShouldReturnTurnstile() {
        // Arrange
        when(turnstileRepository.findById(1L)).thenReturn(Optional.of(turnstile));
        when(turnstileMapper.turnstileToDtoTurnstile(turnstile)).thenReturn(dtoTurnstile);

        // Act
        DtoTurnstile result = turnstileService.getOneTurnstile(1L);

        // Assert
        assertNotNull(result);
        assertEquals(dtoTurnstile.getTurnstileName(), result.getTurnstileName());
        
        verify(turnstileRepository).findById(1L);
        verify(turnstileMapper).turnstileToDtoTurnstile(turnstile);
    }

    @Test
    void getOneTurnstile_WhenTurnstileNotExists_ShouldThrowException() {
        // Arrange
        when(turnstileRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> turnstileService.getOneTurnstile(1L));
        verify(turnstileRepository).findById(1L);
    }

    @Test
    void saveOneTurnstile_WithValidData_ShouldSaveTurnstile() {
        // Arrange
        dtoTurnstileIU.setGateId(1L);
        
        when(turnstileRepository.existsByTurnstileName(dtoTurnstileIU.getTurnstileName())).thenReturn(false);
        when(gateService.checkIfGateExists(dtoTurnstileIU.getGateId())).thenReturn(gate);
        when(turnstileMapper.dtoTurnstileIUToTurnstile(dtoTurnstileIU)).thenReturn(turnstile);
        when(turnstileRepository.save(turnstile)).thenReturn(turnstile);
        when(turnstileMapper.turnstileToDtoTurnstile(turnstile)).thenReturn(dtoTurnstile);

        // Act
        DtoTurnstile result = turnstileService.saveOneTurnstile(dtoTurnstileIU);

        // Assert
        assertNotNull(result);
        assertEquals(dtoTurnstile.getTurnstileName(), result.getTurnstileName());
        
        verify(turnstileRepository).existsByTurnstileName(dtoTurnstileIU.getTurnstileName());
        verify(gateService).checkIfGateExists(dtoTurnstileIU.getGateId());
        verify(turnstileMapper).dtoTurnstileIUToTurnstile(dtoTurnstileIU);
        verify(turnstileRepository).save(turnstile);
        verify(turnstileMapper).turnstileToDtoTurnstile(turnstile);
    }

    @Test
    void saveOneTurnstile_WithEmptyName_ShouldThrowValidationException() {
        // Arrange
        dtoTurnstileIU.setTurnstileName(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> turnstileService.saveOneTurnstile(dtoTurnstileIU));
    }

    @Test
    void saveOneTurnstile_WithExistingName_ShouldThrowValidationException() {
        // Arrange
        when(turnstileRepository.existsByTurnstileName(dtoTurnstileIU.getTurnstileName())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> turnstileService.saveOneTurnstile(dtoTurnstileIU));
        verify(turnstileRepository).existsByTurnstileName(dtoTurnstileIU.getTurnstileName());
    }

    @Test
    void updateOneTurnstile_WithValidData_ShouldUpdateTurnstile() {
        // Arrange
        DtoTurnstileIU updatedTurnstileIU = new DtoTurnstileIU();
        updatedTurnstileIU.setTurnstileName("Updated Turnstile");
        updatedTurnstileIU.setGateId(1L);
        
        when(turnstileRepository.findById(1L)).thenReturn(Optional.of(turnstile));
        when(turnstileRepository.existsByTurnstileName(updatedTurnstileIU.getTurnstileName())).thenReturn(false);
        when(gateService.checkIfGateExists(updatedTurnstileIU.getGateId())).thenReturn(gate);
        when(turnstileRepository.save(turnstile)).thenReturn(turnstile);
        when(turnstileMapper.turnstileToDtoTurnstile(turnstile)).thenReturn(dtoTurnstile);

        // Act
        DtoTurnstile result = turnstileService.updateOneTurnstile(1L, updatedTurnstileIU);

        // Assert
        assertNotNull(result);
        verify(turnstileRepository).findById(1L);
        verify(turnstileRepository).existsByTurnstileName(updatedTurnstileIU.getTurnstileName());
        verify(gateService).checkIfGateExists(updatedTurnstileIU.getGateId());
        verify(turnstileRepository).save(turnstile);
        verify(turnstileMapper).turnstileToDtoTurnstile(turnstile);
    }

    @Test
    void updateOneTurnstile_WithNonExistingTurnstile_ShouldThrowException() {
        // Arrange
        when(turnstileRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> turnstileService.updateOneTurnstile(1L, dtoTurnstileIU));
        verify(turnstileRepository).findById(1L);
    }

    @Test
    void updateOneTurnstile_WithExistingName_ShouldThrowValidationException() {
        // Arrange
        DtoTurnstileIU updatedTurnstileIU = new DtoTurnstileIU();
        updatedTurnstileIU.setTurnstileName("Updated Turnstile");
        
        when(turnstileRepository.findById(1L)).thenReturn(Optional.of(turnstile));
        when(turnstileRepository.existsByTurnstileName(updatedTurnstileIU.getTurnstileName())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> turnstileService.updateOneTurnstile(1L, updatedTurnstileIU));
        verify(turnstileRepository).findById(1L);
        verify(turnstileRepository).existsByTurnstileName(updatedTurnstileIU.getTurnstileName());
    }

    @Test
    void deleteOneTurnstile_WhenTurnstileExists_ShouldDeleteTurnstile() {
        // Arrange
        when(turnstileRepository.findById(1L)).thenReturn(Optional.of(turnstile));

        // Act
        turnstileService.deleteOneTurnstile(1L);

        // Assert
        verify(turnstileRepository).findById(1L);
        verify(turnstileRepository).delete(turnstile);
    }

    @Test
    void deleteOneTurnstile_WhenTurnstileNotExists_ShouldThrowException() {
        // Arrange
        when(turnstileRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> turnstileService.deleteOneTurnstile(1L));
        verify(turnstileRepository).findById(1L);
    }

    @Test
    void passTurnstile_WithValidData_ShouldPassTurnstileSuccessfully() {
        // Arrange
        DtoTurnstilePassageFullRequest request = new DtoTurnstilePassageFullRequest();
        request.setWantedToEnterTurnstileId(1L);
        request.setPersonelId(1L);
        request.setOperationTimeStr("10:00:00");
        request.setOperationType(OperationType.IN);
        
        turnstile.setGateId(gate);

        when(turnstileRepository.findById(1L)).thenReturn(Optional.of(turnstile));
        when(personelService.getPersonelWithCache(1L)).thenReturn(personel);
        doNothing().when(turnstileRegistrationLogService).saveOneTurnstileRegistrationLog(any(DtoTurnstileRegistrationLogIU.class));
        doNothing().when(redisCacheService).addToDailyTurnstilePassageRecord(anyString(), any(DtoTurnstileBasedPersonnelEntry.class), any(LocalDate.class));
        doNothing().when(kafkaProducerService).sendTurnstilePassageEvent(any(TurnstilePassageEvent.class));

        // Act
        ResponseEntity<String> response = turnstileService.passTurnstile(request);

        // Assert
        assertNotNull(response);
        assertEquals("Turnstile passed successfully", response.getBody());
        
        verify(turnstileRepository).findById(1L);
        verify(personelService).getPersonelWithCache(1L);
        verify(turnstileRegistrationLogService).saveOneTurnstileRegistrationLog(any(DtoTurnstileRegistrationLogIU.class));
        verify(redisCacheService).addToDailyTurnstilePassageRecord(anyString(), any(DtoTurnstileBasedPersonnelEntry.class), any(LocalDate.class));
    }

    @Test
    void passTurnstile_WithLateArrival_ShouldSendNotification() {
        // Arrange
        DtoTurnstilePassageFullRequest request = new DtoTurnstilePassageFullRequest();
        request.setWantedToEnterTurnstileId(1L);
        request.setPersonelId(1L);
        request.setOperationTimeStr("09:30:00"); // Geç giriş (9:15'ten sonra)
        request.setOperationType(OperationType.IN);
        
        turnstile.setGateId(gate);

        when(turnstileRepository.findById(1L)).thenReturn(Optional.of(turnstile));
        when(personelService.getPersonelWithCache(1L)).thenReturn(personel);
        doNothing().when(turnstileRegistrationLogService).saveOneTurnstileRegistrationLog(any(DtoTurnstileRegistrationLogIU.class));
        doNothing().when(redisCacheService).addToDailyTurnstilePassageRecord(anyString(), any(DtoTurnstileBasedPersonnelEntry.class), any(LocalDate.class));
        doNothing().when(kafkaProducerService).sendTurnstilePassageEvent(any(TurnstilePassageEvent.class));

        // Act
        ResponseEntity<String> response = turnstileService.passTurnstile(request);

        // Assert
        assertNotNull(response);
        assertEquals("Turnstile passed successfully", response.getBody());
        
        verify(turnstileRepository).findById(1L);
        verify(personelService).getPersonelWithCache(1L);
        verify(turnstileRegistrationLogService).saveOneTurnstileRegistrationLog(any(DtoTurnstileRegistrationLogIU.class));
        verify(redisCacheService).addToDailyTurnstilePassageRecord(anyString(), any(DtoTurnstileBasedPersonnelEntry.class), any(LocalDate.class));
        verify(kafkaProducerService).sendTurnstilePassageEvent(any(TurnstilePassageEvent.class));
    }

    @Test
    void passTurnstile_WithNonExistingTurnstile_ShouldThrowException() {
        // Arrange
        DtoTurnstilePassageFullRequest request = new DtoTurnstilePassageFullRequest();
        request.setWantedToEnterTurnstileId(1L);
        
        when(turnstileRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> turnstileService.passTurnstile(request));
        verify(turnstileRepository).findById(1L);
    }

    @Test
    void getDailyTurnstilePassageRecords_ShouldReturnRecords() {
        // Arrange
        Map<String, List<DtoTurnstileBasedPersonnelEntry>> recordsMap = new HashMap<>();
        List<DtoTurnstileBasedPersonnelEntry> entries = new ArrayList<>();
        
        DtoTurnstileBasedPersonnelEntry entry = new DtoTurnstileBasedPersonnelEntry();
        entry.setPersonelId(1L);
        entry.setPersonelName("Test Personel");
        entry.setOperationTime(LocalDateTime.now());
        entries.add(entry);
        
        recordsMap.put("Test Turnstile", entries);
        
        when(redisCacheService.getDailyTurnstilePassageRecordsByDate(any(LocalDate.class))).thenReturn(recordsMap);

        // Act
        Map<String, List<DtoTurnstileBasedPersonnelEntry>> result = turnstileService.getDailyTurnstilePassageRecords();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("Test Turnstile"));
        assertEquals(1, result.get("Test Turnstile").size());
        
        verify(redisCacheService).getDailyTurnstilePassageRecordsByDate(any(LocalDate.class));
    }
} 
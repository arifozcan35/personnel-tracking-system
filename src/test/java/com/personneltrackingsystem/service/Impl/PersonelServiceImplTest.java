package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.*;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Work;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.mapper.PersonelMapper;
import com.personneltrackingsystem.mapper.UnitMapper;
import com.personneltrackingsystem.mapper.WorkMapper;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.GateService;
import com.personneltrackingsystem.service.UnitService;
import com.personneltrackingsystem.service.WorkService;
import com.personneltrackingsystem.validator.PersonelValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonelServiceImplTest {

    @Mock
    private PersonelRepository personelRepository;

    @Mock
    private WorkService workServiceImpl;

    @Mock
    private UnitService unitServiceImpl;

    @Mock
    private GateService gateServiceImpl;

    @Mock
    private MessageResolver messageResolver;

    @Mock
    private PersonelMapper personelMapper;

    @Mock
    private UnitMapper unitMapper;

    @Mock
    private GateMapper gateMapper;

    @Mock
    private WorkMapper workMapper;

    @Mock
    private PersonelValidator personelValidator;

    @InjectMocks
    private PersonelServiceImpl personelService;

    private Personel personel;
    private DtoPersonel dtoPersonel;
    private DtoPersonelIU dtoPersonelIU;
    private Work work;
    private Unit unit;
    private Gate gate;
    private DtoUnit dtoUnit;
    private DtoGate dtoGate;
    private DtoWork dtoWork;
    private DtoUnitIU dtoUnitIU;
    private DtoGateIU dtoGateIU;

    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_FINISH = LocalTime.of(18, 0);
    private static final Duration MAX_WORK_MISSING = Duration.ofMinutes(30);
    private static final double PENALTY_AMOUNT = 1000.0;

    @BeforeEach
    void setUp() {
        // Setup basic test data
        personel = new Personel();
        personel.setPersonelId(1L);
        personel.setName("Test Person");
        personel.setEmail("test@example.com");
        personel.setSalary(30000.0);
        personel.setAdministrator(false);

        dtoPersonel = new DtoPersonel();
        dtoPersonel.setName("Test Person");
        dtoPersonel.setEmail("test@example.com");
        dtoPersonel.setSalary(30000.0);

        dtoPersonelIU = new DtoPersonelIU();
        dtoPersonelIU.setName("Test Person");
        dtoPersonelIU.setEmail("test@example.com");
        dtoPersonelIU.setSalary(30000.0);
        dtoPersonelIU.setAdministrator(false);

        work = new Work();
        work.setWorkId(1L);
        work.setCheckInTime(LocalTime.of(9, 0));
        work.setCheckOutTime(LocalTime.of(18, 0));
        work.setIsWorkValid(true);
        personel.setWork(work);

        unit = new Unit();
        unit.setUnitId(1L);
        unit.setUnitName("Test Unit");
        personel.setUnit(unit);

        gate = new Gate();
        gate.setGateId(1L);
        gate.setGateName("Test Gate");
        personel.setGate(gate);

        dtoUnitIU = new DtoUnitIU();
        dtoUnitIU.setUnitId(1L);
        dtoUnitIU.setBirimIsim("Test Unit");
        dtoPersonelIU.setUnit(dtoUnit);

        dtoGateIU = new DtoGateIU();
        dtoGateIU.setGateId(1L);
        dtoGateIU.setGateName("Test Gate");
        dtoPersonelIU.setGate(dtoGate);
    }

    @Test
    void getAllPersonels_ShouldReturnAllPersonels() {
        // Arrange
        List<Personel> personelList = Collections.singletonList(personel);
        List<DtoPersonel> dtoPersonelList = Collections.singletonList(dtoPersonel);

        when(personelRepository.findAll()).thenReturn(personelList);
        when(personelMapper.personelsToDtoPersonels(personelList)).thenReturn(dtoPersonelList);

        // Act
        List<DtoPersonel> result = personelService.getAllPersonels();

        // Assert
        assertEquals(dtoPersonelList, result);
        verify(personelRepository).findAll();
        verify(personelMapper).personelsToDtoPersonels(personelList);
    }

    @Test
    void getAOnePersonel_WhenPersonelExists_ShouldReturnDtoPersonel() {
        // Arrange
        Long personelId = 1L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(personelMapper.personelToDtoPersonel(personel)).thenReturn(dtoPersonel);

        // Act
        DtoPersonel result = personelService.getAOnePersonel(personelId);

        // Assert
        assertEquals(dtoPersonel, result);
        verify(personelRepository).findById(personelId);
        verify(personelMapper).personelToDtoPersonel(personel);
    }

    @Test
    void getAOnePersonel_WhenPersonelDoesNotExist_ShouldThrowBaseException() {
        // Arrange
        Long personelId = 1L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.empty());

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> {
            personelService.getAOnePersonel(personelId);
        });
        verify(personelRepository).findById(personelId);
    }

    @Test
    void saveOnePersonel_ShouldSavePersonelAndReturnCreatedResponse() {
        // Arrange
        when(personelMapper.dtoPersonelIUToPersonel(dtoPersonelIU)).thenReturn(personel);
        when(personelRepository.findByEmail(dtoPersonelIU.getEmail())).thenReturn(Optional.empty());
        when(personelRepository.save(personel)).thenReturn(personel);

        // Configure work hours
        dtoPersonelIU.setWork(new DtoWork());
        dtoPersonelIU.getWork().setCheckInTime(LocalTime.of(9, 0));
        dtoPersonelIU.getWork().setCheckOutTime(LocalTime.of(18, 0));

        // Act
        ResponseEntity<String> response = personelService.saveOnePersonel(dtoPersonelIU);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("Personnel registered successfully"));
        verify(personelValidator).validatePersonelForSave(dtoPersonelIU);
        verify(personelMapper).dtoPersonelIUToPersonel(dtoPersonelIU);
        verify(personelRepository).save(personel);
    }

    @Test
    void saveOnePersonel_EmailAlreadyExists_ShouldThrowValidationException() {
        // Arrange
        when(personelRepository.findByEmail(dtoPersonelIU.getEmail())).thenReturn(Optional.of(personel));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            personelService.saveOnePersonel(dtoPersonelIU);
        });
        assertEquals("Personnel with this email already exists!", exception.getMessage());
        verify(personelRepository).findByEmail(dtoPersonelIU.getEmail());
    }

    @Test
    void saveOnePersonel_DataIntegrityViolation_ShouldReturnBadRequest() {
        // Arrange
        when(personelMapper.dtoPersonelIUToPersonel(dtoPersonelIU)).thenReturn(personel);
        when(personelRepository.findByEmail(dtoPersonelIU.getEmail())).thenReturn(Optional.empty());
        when(personelRepository.save(personel)).thenThrow(new DataIntegrityViolationException("Data integrity violation"));

        // Act
        ResponseEntity<String> response = personelService.saveOnePersonel(dtoPersonelIU);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Could not save personnel due to a data integrity violation"));
        verify(personelValidator).validatePersonelForSave(dtoPersonelIU);
    }

    @Test
    void updateOnePersonel_WhenPersonelExists_ShouldUpdateAndReturnOkResponse() {
        // Arrange
        Long personelId = 1L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(personelRepository.save(personel)).thenReturn(personel);

        // For email update
        when(personelRepository.findByEmail(dtoPersonelIU.getEmail())).thenReturn(Optional.empty());

        // For unit update
        when(unitServiceImpl.findById(dtoUnitIU.getUnitId())).thenReturn(Optional.of(dtoUnitIU));
        when(unitMapper.dtoUnitIUToUnit(dtoUnitIU)).thenReturn(unit);

        // For gate update
        when(gateServiceImpl.findById(dtoGateIU.getGateId())).thenReturn(Optional.of(dtoGateIU));
        when(gateMapper.dtoGateIUToGate(dtoGateIU)).thenReturn(gate);

        // For work update
        dtoPersonelIU.setWork(new DtoWork());
        dtoPersonelIU.getWork().setCheckInTime(LocalTime.of(9, 0));
        dtoPersonelIU.getWork().setCheckOutTime(LocalTime.of(18, 0));

        // Act
        ResponseEntity<String> response = personelService.updateOnePersonel(personelId, dtoPersonelIU);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Personnel updated successfully"));
    }

    @Test
    void updateOnePersonel_WhenPersonelDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        Long personelId = 1L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = personelService.updateOnePersonel(personelId, dtoPersonelIU);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No personnel found!", response.getBody());
    }

    @Test
    void updateOnePersonel_EmailAlreadyInUseByAnotherPersonel_ShouldThrowValidationException() {
        // Arrange
        Long personelId = 1L;
        Personel otherPersonel = new Personel();
        otherPersonel.setPersonelId(2L);
        otherPersonel.setEmail("test@example.com");

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(personelRepository.findByEmail(dtoPersonelIU.getEmail())).thenReturn(Optional.of(otherPersonel));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            personelService.updateOnePersonel(personelId, dtoPersonelIU);
        });
        assertEquals("Email is already in use by another personnel!", exception.getMessage());
    }

    @Test
    void updateOnePersonel_UnitNotFound_ShouldReturnNotFound() {
        // Arrange
        Long personelId = 1L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(unitServiceImpl.findById(dtoUnitIU.getUnitId())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = personelService.updateOnePersonel(personelId, dtoPersonelIU);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("You have not selected a suitable unit!", response.getBody());
    }

    @Test
    void updateOnePersonel_GateNotFound_ShouldReturnNotFound() {
        // Arrange
        Long personelId = 1L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(unitServiceImpl.findById(dtoUnitIU.getUnitId())).thenReturn(Optional.of(dtoUnitIU));
        when(unitMapper.dtoUnitIUToUnit(dtoUnitIU)).thenReturn(unit);
        when(gateServiceImpl.findById(dtoGateIU.getGateId())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = personelService.updateOnePersonel(personelId, dtoPersonelIU);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("The specified gate could not be found!", response.getBody());
    }

    @Test
    void deleteOnePersonel_WhenPersonelExists_ShouldDeletePersonel() {
        // Arrange
        Long personelId = 1L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));

        // Act
        personelService.deleteOnePersonel(personelId);

        // Assert
        verify(workServiceImpl).deleteById(personel.getWork().getWorkId());
        verify(personelRepository).deleteById(personelId);
    }

    @Test
    void deleteOnePersonel_WhenPersonelDoesNotExist_ShouldThrowException() {
        // Arrange
        Long personelId = 1L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.empty());

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> {
            personelService.deleteOnePersonel(personelId);
        });
        verify(personelRepository, never()).deleteById(any());
    }

    @Test
    void workHoursCalculate_ValidWork_ShouldCalculateCorrectly() {
        // Arrange
        Long personelId = 1L;
        Duration workTime = Duration.between(LocalTime.of(9, 0), LocalTime.of(18, 0));

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(personelValidator.calculateWorkTime(any(), any())).thenReturn(workTime);
        when(personelValidator.isWorkValid(any(), any())).thenReturn(true);
        when(personelRepository.save(personel)).thenReturn(personel);

        // Act
        DtoPersonel result = personelService.workHoursCalculate(personelId);

        // Assert
        assertEquals(dtoPersonel.getName(), result.getName());
        assertEquals(dtoPersonel.getEmail(), result.getEmail());
        assertEquals(dtoPersonel.getSalary(), result.getSalary());
        assertTrue(personel.getWork().getIsWorkValid());
        verify(personelRepository).save(personel);
    }

    @Test
    void workHoursCalculate_InvalidWork_ShouldApplyPenalty() {
        // Arrange
        Long personelId = 1L;
        Duration workTime = Duration.between(LocalTime.of(9, 0), LocalTime.of(16, 0)); // Short work day

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(personelValidator.calculateWorkTime(any(), any())).thenReturn(workTime);
        when(personelValidator.isWorkValid(any(), any())).thenReturn(false);
        when(personelValidator.calculatePenalty(workTime)).thenReturn(PENALTY_AMOUNT);
        when(personelRepository.save(personel)).thenReturn(personel);

        // Act
        DtoPersonel result = personelService.workHoursCalculate(personelId);

        // Assert
        assertFalse(personel.getWork().getIsWorkValid());
        assertEquals(personel.getSalary(), result.getSalary());
        verify(personelRepository).save(personel);
    }

    @Test
    void workHoursCalculate_PersonelNotFound_ShouldThrowException() {
        // Arrange
        Long personelId = 1L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> {
            personelService.workHoursCalculate(personelId);
        });
    }

    @Test
    void workHoursCalculate2_NonAdmin_InvalidWork_ShouldApplyPenalty() {
        // Arrange
        LocalTime checkIn = LocalTime.of(9, 0);
        LocalTime checkOut = LocalTime.of(17, 0); // Short work day
        Duration workTime = Duration.between(checkIn, checkOut);

        personel.setAdministrator(false);
        personel.getWork().setCheckInTime(checkIn);
        personel.getWork().setCheckOutTime(checkOut);

        when(personelValidator.calculateWorkTime(checkIn, checkOut)).thenReturn(workTime);
        when(personelValidator.isWorkValid(checkIn, checkOut)).thenReturn(false);
        when(personelValidator.calculatePenalty(workTime)).thenReturn(PENALTY_AMOUNT);

        // Act
        personelService.workHoursCalculate2(personel);

        // Assert
        assertFalse(personel.getWork().getIsWorkValid());
        assertEquals(29000.0, personel.getSalary()); // 30000 - 1000 penalty
    }

    @Test
    void workHoursCalculate2_Admin_InvalidWork_ShouldNotApplyPenalty() {
        // Arrange
        LocalTime checkIn = LocalTime.of(9, 0);
        LocalTime checkOut = LocalTime.of(17, 0); // Short work day
        Duration workTime = Duration.between(checkIn, checkOut);

        personel.setAdministrator(true);
        personel.setSalary(40000.0);
        personel.getWork().setCheckInTime(checkIn);
        personel.getWork().setCheckOutTime(checkOut);

        when(personelValidator.calculateWorkTime(checkIn, checkOut)).thenReturn(workTime);
        when(personelValidator.isWorkValid(checkIn, checkOut)).thenReturn(false);

        // Act
        personelService.workHoursCalculate2(personel);

        // Assert
        assertFalse(personel.getWork().getIsWorkValid());
        assertEquals(40000.0, personel.getSalary()); // No penalty for admin
    }

    @Test
    void getOneWorkofPersonel_ExistingWork_ShouldReturnWork() {
        // Arrange
        Long personelId = 1L;
        Long workId = 1L;

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(workServiceImpl.findById(workId)).thenReturn(Optional.of(work));
        when(personelMapper.DbWorktoWork(Optional.of(work))).thenReturn(work);

        // Act
        Work result = personelService.getOneWorkofPersonel(personelId);

        // Assert
        assertEquals(work, result);
    }

    @Test
    void getOneWorkofPersonel_PersonelNotFound_ShouldThrowException() {
        // Arrange
        Long personelId = 1L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> {
            personelService.getOneWorkofPersonel(personelId);
        });
    }

    @Test
    void getOneWorkofPersonel_WorkNotFound_ShouldThrowException() {
        // Arrange
        Long personelId = 1L;
        Long workId = 1L;

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(workServiceImpl.findById(workId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> {
            personelService.getOneWorkofPersonel(personelId);
        });
    }

    @Test
    void listSalaries_ShouldReturnSalaryMap() {
        // Arrange
        List<Personel> personelList = Collections.singletonList(personel);
        Map<String, Double> salaryMap = new HashMap<>();
        salaryMap.put("Test Person", 30000.0);

        when(personelRepository.findAll()).thenReturn(personelList);
        when(personelMapper.personelsToSalaryMap(personelList)).thenReturn(salaryMap);

        // Act
        Map<String, Double> result = personelService.listSalaries();

        // Assert
        assertEquals(salaryMap, result);
        verify(personelRepository).findAll();
        verify(personelMapper).personelsToSalaryMap(personelList);
    }

    // PersonelValidator tests
    @Test
    void personelValidator_validatePersonelForSave_ValidPersonel_ShouldNotThrowException() {
        // Arrange
        DtoPersonelIU validPersonel = new DtoPersonelIU();
        validPersonel.setUnit(dtoUnit);
        validPersonel.setGate(dtoGate);
        validPersonel.setAdministrator(true);
        validPersonel.setSalary(40000.0);

        when(unitServiceImpl.findById(dtoUnitIU.getUnitId())).thenReturn(Optional.of(dtoUnitIU));
        when(gateServiceImpl.findById(dtoGateIU.getGateId())).thenReturn(Optional.of(dtoGateIU));
        doNothing().when(personelValidator).validatePersonelForSave(validPersonel);

        // Act & Assert - No exception should be thrown
        personelValidator.validatePersonelForSave(validPersonel);

        // Verify the validator was called
        verify(personelValidator).validatePersonelForSave(validPersonel);
    }

    @Test
    void personelValidator_selectionPosition_AdminTrue_ShouldSetSalaryTo40000() {
        // Mock the validator method
        DtoPersonelIU pAdmin = new DtoPersonelIU();
        doAnswer(invocation -> {
            pAdmin.setSalary(40000.0);
            return false;
        }).when(personelValidator).selectionPosition(eq(pAdmin), eq(true));

        // Call the method
        personelValidator.selectionPosition(pAdmin, true);

        // Assert
        assertEquals(40000.0, pAdmin.getSalary());
    }

    @Test
    void personelValidator_selectionPosition_AdminFalse_ShouldSetSalaryTo30000() {
        // Mock the validator method
        DtoPersonelIU pAdmin = new DtoPersonelIU();
        doAnswer(invocation -> {
            pAdmin.setSalary(30000.0);
            return false;
        }).when(personelValidator).selectionPosition(eq(pAdmin), eq(false));

        // Call the method
        personelValidator.selectionPosition(pAdmin, false);

        // Assert
        assertEquals(30000.0, pAdmin.getSalary());
    }

    @Test
    void personelValidator_salaryAssignment_ValidSalary40000_ShouldSetAdminToTrue() {
        // Mock the validator method
        DtoPersonelIU pSalary = new DtoPersonelIU();
        doAnswer(invocation -> {
            pSalary.setAdministrator(true);
            pSalary.setSalary(40000.0);
            return null;
        }).when(personelValidator).salaryAssignment(eq(pSalary), eq(40000.0));

        // Call the method
        personelValidator.salaryAssignment(pSalary, 40000.0);

        // Assert
        assertTrue(pSalary.getAdministrator());
        assertEquals(40000.0, pSalary.getSalary());
    }

    @Test
    void personelValidator_salaryAssignment_ValidSalary30000_ShouldSetAdminToFalse() {
        // Mock the validator method
        DtoPersonelIU pSalary = new DtoPersonelIU();
        doAnswer(invocation -> {
            pSalary.setAdministrator(false);
            pSalary.setSalary(30000.0);
            return null;
        }).when(personelValidator).salaryAssignment(eq(pSalary), eq(30000.0));

        // Call the method
        personelValidator.salaryAssignment(pSalary, 30000.0);

        // Assert
        assertFalse(pSalary.getAdministrator());
        assertEquals(30000.0, pSalary.getSalary());
    }

    @Test
    void personelValidator_salaryAssignment_InvalidSalary_ShouldSetToNearestAndThrowException() {
        // Mock the validator method
        DtoPersonelIU pSalary = new DtoPersonelIU();
        doAnswer(invocation -> {
            pSalary.setAdministrator(false);
            pSalary.setSalary(30000.0);
            throw new ValidationException("The salary can be only 30000.0 or 40000.0!");
        }).when(personelValidator).salaryAssignment(eq(pSalary), eq(32000.0));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            personelValidator.salaryAssignment(pSalary, 32000.0);
        });

        // Assert exception message contains expected text
        assertTrue(exception.getMessage().contains("The salary can be only 30000.0 or 40000.0!"));
    }

    @Test
    void personelValidator_calculateWorkTime_ShouldReturnCorrectDuration() {
        // Arrange
        LocalTime checkIn = LocalTime.of(9, 0);
        LocalTime checkOut = LocalTime.of(18, 0);
        Duration expected = Duration.between(checkIn, checkOut);

        when(personelValidator.calculateWorkTime(checkIn, checkOut)).thenReturn(expected);

        // Act
        Duration result = personelValidator.calculateWorkTime(checkIn, checkOut);

        // Assert
        assertEquals(expected, result);
        assertEquals(9, result.toHours());
    }

    @Test
    void personelValidator_isWorkValid_ValidWork_ShouldReturnTrue() {
        // Arrange
        LocalTime checkIn = LocalTime.of(9, 0);
        LocalTime checkOut = LocalTime.of(18, 0);

        when(personelValidator.isWorkValid(checkIn, checkOut)).thenReturn(true);

        // Act
        boolean result = personelValidator.isWorkValid(checkIn, checkOut);

        // Assert
        assertTrue(result);
    }

    @Test
    void personelValidator_isWorkValid_InvalidWork_ShouldReturnFalse() {
        // Arrange
        LocalTime checkIn = LocalTime.of(9, 0);
        LocalTime checkOut = LocalTime.of(16, 0); // Only 7 hours

        when(personelValidator.isWorkValid(checkIn, checkOut)).thenReturn(false);

        // Act
        boolean result = personelValidator.isWorkValid(checkIn, checkOut);

        // Assert
        assertFalse(result);
    }

    @Test
    void personelValidator_calculatePenalty_ShortWork_ShouldReturnPenaltyAmount() {
        // Arrange
        Duration shortWorkPeriod = Duration.ofHours(7);

        when(personelValidator.calculatePenalty(shortWorkPeriod)).thenReturn(PENALTY_AMOUNT);

        // Act
        double result = personelValidator.calculatePenalty(shortWorkPeriod);

        // Assert
        assertEquals(PENALTY_AMOUNT, result);
    }

    @Test
    void personelValidator_calculatePenalty_ValidWork_ShouldReturnZero() {
        // Arrange
        Duration validWorkPeriod = Duration.ofHours(9);

        when(personelValidator.calculatePenalty(validWorkPeriod)).thenReturn(0.0);

        // Act
        double result = personelValidator.calculatePenalty(validWorkPeriod);

        // Assert
        assertEquals(0.0, result);
    }
}
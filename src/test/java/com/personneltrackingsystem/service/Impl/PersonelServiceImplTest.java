package com.personneltrackingsystem.service.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Work;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.mapper.PersonelMapper;
import com.personneltrackingsystem.mapper.UnitMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.BeanUtils;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonelServiceImplTest {

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
    private PersonelValidator personelValidator;

    @InjectMocks
    private PersonelServiceImpl personelService;

    private Personel mockPersonel;
    private DtoPersonel mockDtoPersonel;
    private DtoPersonelIU mockDtoPersonelIU;

    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_FINISH = LocalTime.of(18, 0);
    private static final Duration MAX_WORK_MISSING = Duration.ofMinutes(15);
    private static final double PENALTY_AMOUNT = 500.0;

    @BeforeEach
    void setUp() {
        mockPersonel = new Personel();
        mockPersonel.setPersonelId(1L);
        mockPersonel.setName("Test Personel");
        mockPersonel.setEmail("test@example.com");
        mockPersonel.setSalary(30000.0);

        mockDtoPersonel = new DtoPersonel();
        mockDtoPersonel.setName("Test Personel");
        mockDtoPersonel.setEmail("test@example.com");

        mockDtoPersonelIU = new DtoPersonelIU();
        mockDtoPersonelIU.setName("Test Personel");
        mockDtoPersonelIU.setEmail("test@example.com");
    }

    @Test
    void testGetAllPersonels() {
        List<Personel> personelList = Arrays.asList(mockPersonel);
        List<DtoPersonel> expectedDtoList = Arrays.asList(mockDtoPersonel);

        when(personelRepository.findAll()).thenReturn(personelList);
        when(personelMapper.personelsToDtoPersonels(personelList)).thenReturn(expectedDtoList);

        List<DtoPersonel> result = personelService.getAllPersonels();

        assertEquals(expectedDtoList, result);
        verify(personelRepository).findAll();
        verify(personelMapper).personelsToDtoPersonels(personelList);
    }

    @Test
    void testGetAOnePersonel_Exists() {
        when(personelRepository.findById(1L)).thenReturn(Optional.of(mockPersonel));
        when(personelMapper.personelToDtoPersonel(mockPersonel)).thenReturn(mockDtoPersonel);

        DtoPersonel result = personelService.getAOnePersonel(1L);

        assertEquals(mockDtoPersonel, result);
        verify(personelRepository).findById(1L);
        verify(personelMapper).personelToDtoPersonel(mockPersonel);
    }

    @Test
    void testGetAOnePersonel_NotExists() {
        when(personelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> personelService.getAOnePersonel(1L));
        verify(personelRepository).findById(1L);
    }

    @Test
    void testSaveOnePersonel_Success() {
        Unit mockUnit = new Unit();
        mockUnit.setUnitId(1L);
        Gate mockGate = new Gate();
        mockGate.setGateId(1L);
        Work mockWork = new Work();
        mockWork.setCheckInTime(WORK_START);
        mockWork.setCheckOutTime(WORK_FINISH);

        mockDtoPersonelIU.setUnit(new DtoUnit(mockUnit.getUnitId(), mockUnit.getUnitName()));
        mockDtoPersonelIU.setGate(new DtoGate(mockGate.getGateId(), mockGate.getGateName()));
        mockDtoPersonelIU.setWork(mockWork);
        mockDtoPersonelIU.setAdministrator(false);
        mockDtoPersonelIU.setSalary(30000.0);

        when(unitServiceImpl.findById(mockUnit.getUnitId())).thenReturn(Optional.of(mockUnit));
        when(gateServiceImpl.findById(mockGate.getGateId())).thenReturn(Optional.of(mockGate));
        when(personelRepository.findByEmail(mockDtoPersonelIU.getEmail())).thenReturn(Optional.empty());
        when(personelMapper.dtoPersonelIUToPersonel(mockDtoPersonelIU)).thenReturn(mockPersonel);
        when(workServiceImpl.save(mockWork)).thenReturn(mockWork);
        when(personelRepository.save(mockPersonel)).thenReturn(mockPersonel);

        ResponseEntity<String> result = personelService.saveOnePersonel(mockDtoPersonelIU);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody().contains("Personnel registered successfully"));
        verify(personelRepository).save(mockPersonel);
    }


    @Test
    void testUpdateOnePersonel_Success() {
        Personel existingPersonel = new Personel();
        existingPersonel.setPersonelId(1L);

        when(personelRepository.findById(1L)).thenReturn(Optional.of(existingPersonel));
        when(personelRepository.findByEmail(mockDtoPersonelIU.getEmail())).thenReturn(Optional.empty());
        when(personelRepository.save(existingPersonel)).thenReturn(existingPersonel);

        ResponseEntity<String> result = personelService.updateOnePersonel(1L, mockDtoPersonelIU);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().contains("Personnel updated successfully"));
        verify(personelRepository).save(existingPersonel);
    }

    @Test
    void testDeleteOnePersonel_Success() {
        Work mockWork = new Work();
        mockWork.setWorkId(1L);
        mockPersonel.setWork(mockWork);

        when(personelRepository.findById(1L)).thenReturn(Optional.of(mockPersonel));

        personelService.deleteOnePersonel(1L);

        verify(workServiceImpl).deleteById(mockWork.getWorkId());
        verify(personelRepository).deleteById(1L);
    }

    @Test
    void testCalculateSalaryByPersonelId_Success() {
        Work mockWork = new Work();
        mockWork.setCheckInTime(WORK_START);
        mockWork.setCheckOutTime(WORK_FINISH);
        mockPersonel.setWork(mockWork);

        when(personelRepository.findById(1L)).thenReturn(Optional.of(mockPersonel));

        DtoPersonel result = personelService.calculateSalaryByPersonelId(1L);

        assertNotNull(result);
        verify(personelRepository).findById(1L);
    }

    @Test
    void testListSalaries() {
        when(personelRepository.findAll()).thenReturn(Arrays.asList(mockPersonel));

        Map<String, Double> salaries = personelService.listSalaries();

        assertFalse(salaries.isEmpty());
        assertEquals(30000.0, salaries.get("test@example.com"));
    }

    @Test
    void testCalculateWorkTime() {
        LocalTime checkIn = LocalTime.of(9, 0);
        LocalTime checkOut = LocalTime.of(17, 0);

        Duration workTime = personelService.calculateWorkTime(checkIn, checkOut);

        assertEquals(Duration.ofHours(8), workTime);
    }

    @Test
    void testIsWorkValid_Valid() {

        LocalTime checkIn = WORK_START;
        LocalTime checkOut = WORK_FINISH;

        Duration workDuration = Duration.between(checkIn, checkOut);
        Duration workLimit = Duration.between(WORK_START, WORK_FINISH).minus(MAX_WORK_MISSING);

        boolean isValid = personelService.isWorkValid(checkIn, checkOut);

        assertTrue(isValid, "The period of employment must be valid.");
    }

    @Test
    void testIsWorkValid_Invalid() {

        LocalTime checkIn = LocalTime.of(9, 0);
        LocalTime checkOut = LocalTime.of(14, 0);

        boolean isValid = personelService.isWorkValid(checkIn, checkOut);

        assertFalse(isValid, "The period of employment must be invalid.");
    }

    @Test
    void testSelectionPosition() {
        DtoPersonelIU pAdmin = new DtoPersonelIU();

        Boolean result = personelValidator.selectionPosition(pAdmin, true);

        assertEquals(40000.0, pAdmin.getSalary());
        assertFalse(result);
    }

    @Test
    void testSalaryAssignment_MidValue() {
        DtoPersonelIU pSalary = new DtoPersonelIU();

        personelValidator.salaryAssignment(pSalary, 34999.0);

        assertEquals(30000.0, pSalary.getSalary());
        assertFalse(pSalary.getAdministrator());
    }

    @Test
    void testSalaryAssignment_LessThan30000() {
        DtoPersonelIU pSalary = new DtoPersonelIU();

        personelValidator.salaryAssignment(pSalary, 25000.0);

        assertEquals(30000.0, pSalary.getSalary());
        assertFalse(pSalary.getAdministrator());
    }

    @Test
    void testSalaryAssignment_GreaterThan40000() {
        DtoPersonelIU pSalary = new DtoPersonelIU();

        personelValidator.salaryAssignment(pSalary, 45000.0);

        assertEquals(40000.0, pSalary.getSalary());
        assertTrue(pSalary.getAdministrator());
    }

    @Test
    void testSalaryAssignment_Exact30000() {
        DtoPersonelIU pSalary = new DtoPersonelIU();

        personelValidator.salaryAssignment(pSalary, 30000.0);

        assertEquals(30000.0, pSalary.getSalary());
        assertFalse(pSalary.getAdministrator());
    }

    @Test
    void testSalaryAssignment_Exact40000() {
        DtoPersonelIU pSalary = new DtoPersonelIU();

        personelValidator.salaryAssignment(pSalary, 40000.0);

        assertEquals(40000.0, pSalary.getSalary());
        assertTrue(pSalary.getAdministrator());
    }
}
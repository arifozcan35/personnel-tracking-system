package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoBuilding;
import com.personneltrackingsystem.entity.Building;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.BuildingMapper;
import com.personneltrackingsystem.repository.BuildingRepository;
import com.personneltrackingsystem.service.impl.BuildingServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuildingServiceImplTest {

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private BuildingMapper buildingMapper;

    @InjectMocks
    private BuildingServiceImpl buildingService;

    private Building building;
    private DtoBuilding dtoBuilding;
    private final Long buildingId = 1L;
    private final String buildingName = "Test Building";

    @BeforeEach
    void setUp() {
        building = new Building();
        building.setBuildingId(buildingId);
        building.setBuildingName(buildingName);

        dtoBuilding = new DtoBuilding();
        dtoBuilding.setBuildingName(buildingName);
    }

    @Test
    void checkIfBuildingExists_WhenBuildingExists_ShouldReturnBuilding() {
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(building));

        Building result = buildingService.checkIfBuildingExists(buildingId);

        assertNotNull(result);
        assertEquals(buildingId, result.getBuildingId());
        verify(buildingRepository).findById(buildingId);
    }

    @Test
    void checkIfBuildingExists_WhenBuildingNotExists_ShouldThrowException() {
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> buildingService.checkIfBuildingExists(buildingId));
        verify(buildingRepository).findById(buildingId);
    }

    @Test
    void getAllBuildings_ShouldReturnAllBuildings() {
        List<Building> buildings = Arrays.asList(building);
        List<DtoBuilding> dtoBuildings = Arrays.asList(dtoBuilding);

        when(buildingRepository.findAll()).thenReturn(buildings);
        when(buildingMapper.buildingListToDtoBuildingList(buildings)).thenReturn(dtoBuildings);

        List<DtoBuilding> result = buildingService.getAllBuildings();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(buildingRepository).findAll();
        verify(buildingMapper).buildingListToDtoBuildingList(buildings);
    }

    @Test
    void getBuildingById_WhenBuildingExists_ShouldReturnBuilding() {
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(building));
        when(buildingMapper.buildingToDtoBuilding(building)).thenReturn(dtoBuilding);

        Optional<DtoBuilding> result = buildingService.getBuildingById(buildingId);

        assertTrue(result.isPresent());
        assertEquals(buildingName, result.get().getBuildingName());
        verify(buildingRepository).findById(buildingId);
        verify(buildingMapper).buildingToDtoBuilding(building);
    }

    @Test
    void getBuildingById_WhenBuildingNotExists_ShouldThrowException() {
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> buildingService.getBuildingById(buildingId));
        verify(buildingRepository).findById(buildingId);
        verify(buildingMapper, never()).buildingToDtoBuilding(any(Building.class));
    }

    @Test
    void getOneBuilding_WhenBuildingExists_ShouldReturnBuilding() {
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(building));
        when(buildingMapper.buildingToDtoBuilding(building)).thenReturn(dtoBuilding);

        DtoBuilding result = buildingService.getOneBuilding(buildingId);

        assertNotNull(result);
        assertEquals(buildingName, result.getBuildingName());
        verify(buildingRepository).findById(buildingId);
        verify(buildingMapper).buildingToDtoBuilding(building);
    }

    @Test
    void getOneBuilding_WhenBuildingNotExists_ShouldThrowException() {
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> buildingService.getOneBuilding(buildingId));
        verify(buildingRepository).findById(buildingId);
        verify(buildingMapper, never()).buildingToDtoBuilding(any(Building.class));
    }

    @Test
    void saveOneBuilding_WhenValidBuilding_ShouldSaveAndReturnBuilding() {
        when(buildingRepository.existsByBuildingName(buildingName)).thenReturn(false);
        when(buildingMapper.dtoBuildingToBuilding(dtoBuilding)).thenReturn(building);
        when(buildingRepository.save(building)).thenReturn(building);
        when(buildingMapper.buildingToDtoBuilding(building)).thenReturn(dtoBuilding);

        DtoBuilding result = buildingService.saveOneBuilding(dtoBuilding);

        assertNotNull(result);
        assertEquals(buildingName, result.getBuildingName());
        verify(buildingRepository).existsByBuildingName(buildingName);
        verify(buildingMapper).dtoBuildingToBuilding(dtoBuilding);
        verify(buildingRepository).save(building);
        verify(buildingMapper).buildingToDtoBuilding(building);
    }

    @Test
    void saveOneBuilding_WhenBuildingNameEmpty_ShouldThrowException() {
        DtoBuilding emptyNameBuilding = new DtoBuilding();
        emptyNameBuilding.setBuildingName(null);

        assertThrows(ValidationException.class, () -> buildingService.saveOneBuilding(emptyNameBuilding));
        verify(buildingRepository, never()).existsByBuildingName(anyString());
        verify(buildingMapper, never()).dtoBuildingToBuilding(any(DtoBuilding.class));
        verify(buildingRepository, never()).save(any(Building.class));
    }

    @Test
    void saveOneBuilding_WhenBuildingNameExists_ShouldThrowException() {
        when(buildingRepository.existsByBuildingName(buildingName)).thenReturn(true);

        assertThrows(ValidationException.class, () -> buildingService.saveOneBuilding(dtoBuilding));
        verify(buildingRepository).existsByBuildingName(buildingName);
        verify(buildingMapper, never()).dtoBuildingToBuilding(any(DtoBuilding.class));
        verify(buildingRepository, never()).save(any(Building.class));
    }

    @Test
    void updateOneBuilding_WhenBuildingExistsAndNameChanged_ShouldUpdateAndReturnBuilding() {
        String newBuildingName = "Updated Building";
        DtoBuilding updatedDtoBuilding = new DtoBuilding();
        updatedDtoBuilding.setBuildingName(newBuildingName);

        Building existingBuilding = new Building();
        existingBuilding.setBuildingId(buildingId);
        existingBuilding.setBuildingName(buildingName);

        Building updatedBuilding = new Building();
        updatedBuilding.setBuildingId(buildingId);
        updatedBuilding.setBuildingName(newBuildingName);

        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(existingBuilding));
        when(buildingRepository.existsByBuildingName(newBuildingName)).thenReturn(false);
        when(buildingRepository.save(any(Building.class))).thenReturn(updatedBuilding);
        when(buildingMapper.buildingToDtoBuilding(updatedBuilding)).thenReturn(updatedDtoBuilding);

        DtoBuilding result = buildingService.updateOneBuilding(buildingId, updatedDtoBuilding);

        assertNotNull(result);
        assertEquals(newBuildingName, result.getBuildingName());
        verify(buildingRepository).findById(buildingId);
        verify(buildingRepository).existsByBuildingName(newBuildingName);
        verify(buildingRepository).save(any(Building.class));
        verify(buildingMapper).buildingToDtoBuilding(updatedBuilding);
    }

    @Test
    void updateOneBuilding_WhenBuildingNotExists_ShouldThrowException() {
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> buildingService.updateOneBuilding(buildingId, dtoBuilding));
        verify(buildingRepository).findById(buildingId);
        verify(buildingRepository, never()).save(any(Building.class));
    }

    @Test
    void updateOneBuilding_WhenNewNameAlreadyExists_ShouldThrowException() {
        String newBuildingName = "Existing Building";
        DtoBuilding updatedDtoBuilding = new DtoBuilding();
        updatedDtoBuilding.setBuildingName(newBuildingName);

        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(building));
        when(buildingRepository.existsByBuildingName(newBuildingName)).thenReturn(true);

        assertThrows(ValidationException.class, () -> buildingService.updateOneBuilding(buildingId, updatedDtoBuilding));
        verify(buildingRepository).findById(buildingId);
        verify(buildingRepository).existsByBuildingName(newBuildingName);
        verify(buildingRepository, never()).save(any(Building.class));
    }

    @Test
    void deleteOneBuilding_WhenBuildingExists_ShouldDeleteBuilding() {
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(building));
        doNothing().when(buildingRepository).delete(building);

        buildingService.deleteOneBuilding(buildingId);

        verify(buildingRepository).findById(buildingId);
        verify(buildingRepository).delete(building);
    }

    @Test
    void deleteOneBuilding_WhenBuildingNotExists_ShouldThrowException() {
        when(buildingRepository.findById(buildingId)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> buildingService.deleteOneBuilding(buildingId));
        verify(buildingRepository).findById(buildingId);
        verify(buildingRepository, never()).delete(any(Building.class));
    }
} 
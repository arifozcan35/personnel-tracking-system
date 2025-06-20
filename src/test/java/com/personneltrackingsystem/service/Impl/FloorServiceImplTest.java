package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoFloor;
import com.personneltrackingsystem.entity.Building;
import com.personneltrackingsystem.entity.Floor;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.FloorMapper;
import com.personneltrackingsystem.repository.FloorRepository;
import com.personneltrackingsystem.service.BuildingService;
import com.personneltrackingsystem.service.impl.FloorServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FloorServiceImplTest {

    @Mock
    private FloorRepository floorRepository;

    @Mock
    private BuildingService buildingService;

    @Mock
    private FloorMapper floorMapper;

    @InjectMocks
    private FloorServiceImpl floorService;

    private Floor floor;
    private DtoFloor dtoFloor;
    private Building building;
    private final Long floorId = 1L;
    private final Long buildingId = 1L;
    private final String floorName = "3. Kat";

    @BeforeEach
    void setUp() {
        // create test data
        building = new Building();
        building.setBuildingId(buildingId);
        building.setBuildingName("Ana Bina");

        floor = new Floor();
        floor.setFloorId(floorId);
        floor.setFloorName(floorName);
        floor.setBuilding(building);

        dtoFloor = new DtoFloor();
        dtoFloor.setFloorName(floorName);
        dtoFloor.setBuildingId(buildingId);
    }

    @Test
    @DisplayName("Kat ID ile kontrol - Kat mevcut")
    void checkIfFloorExists_WhenFloorExists_ShouldReturnFloor() {
        // Arrange
        when(floorRepository.findById(floorId)).thenReturn(Optional.of(floor));

        // Act
        Floor result = floorService.checkIfFloorExists(floorId);

        // Assert
        assertNotNull(result);
        assertEquals(floorId, result.getFloorId());
        verify(floorRepository, times(1)).findById(floorId);
    }

    @Test
    @DisplayName("Kat ID ile kontrol - Kat mevcut değil")
    void checkIfFloorExists_WhenFloorDoesNotExist_ShouldThrowException() {
        // Arrange
        when(floorRepository.findById(floorId)).thenReturn(Optional.empty());

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> {
            floorService.checkIfFloorExists(floorId);
        });

        assertEquals(MessageType.FLOOR_NOT_FOUND, exception.getMessageType());
        verify(floorRepository, times(1)).findById(floorId);
    }

    @Test
    @DisplayName("Kat ID ile bul - Kat mevcut")
    void findById_WhenFloorExists_ShouldReturnDtoFloor() {
        // Arrange
        when(floorRepository.findById(floorId)).thenReturn(Optional.of(floor));
        when(floorMapper.floorToDtoFloor(floor)).thenReturn(dtoFloor);

        // Act
        Optional<DtoFloor> result = floorService.findById(floorId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(floorName, result.get().getFloorName());
        verify(floorRepository, times(1)).findById(floorId);
        verify(floorMapper, times(1)).floorToDtoFloor(floor);
    }

    @Test
    @DisplayName("Kat ID ile bul - Kat mevcut değil")
    void findById_WhenFloorDoesNotExist_ShouldThrowException() {
        // Arrange
        when(floorRepository.findById(floorId)).thenReturn(Optional.empty());

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> {
            floorService.findById(floorId);
        });

        assertEquals(MessageType.FLOOR_NOT_FOUND, exception.getMessageType());
        verify(floorRepository, times(1)).findById(floorId);
        verify(floorMapper, never()).floorToDtoFloor(any());
    }

    @Test
    @DisplayName("Tüm katları getir")
    void getAllFloors_ShouldReturnAllFloors() {
        // Arrange
        List<Floor> floorList = Arrays.asList(floor);
        List<DtoFloor> dtoFloorList = Arrays.asList(dtoFloor);

        when(floorRepository.findAll()).thenReturn(floorList);
        when(floorMapper.floorListToDtoFloorList(floorList)).thenReturn(dtoFloorList);

        // Act
        List<DtoFloor> result = floorService.getAllFloors();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(floorName, result.get(0).getFloorName());
        verify(floorRepository, times(1)).findAll();
        verify(floorMapper, times(1)).floorListToDtoFloorList(floorList);
    }

    @Test
    @DisplayName("Kat ID ile bir kat getir - Kat mevcut")
    void getOneFloor_WhenFloorExists_ShouldReturnDtoFloor() {
        // Arrange
        when(floorRepository.findById(floorId)).thenReturn(Optional.of(floor));
        when(floorMapper.floorToDtoFloor(floor)).thenReturn(dtoFloor);

        // Act
        DtoFloor result = floorService.getOneFloor(floorId);

        // Assert
        assertNotNull(result);
        assertEquals(floorName, result.getFloorName());
        verify(floorRepository, times(1)).findById(floorId);
        verify(floorMapper, times(1)).floorToDtoFloor(floor);
    }

    @Test
    @DisplayName("Kat ID ile bir kat getir - Kat mevcut değil")
    void getOneFloor_WhenFloorDoesNotExist_ShouldThrowException() {
        // Arrange
        when(floorRepository.findById(floorId)).thenReturn(Optional.empty());

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> {
            floorService.getOneFloor(floorId);
        });

        assertEquals(MessageType.FLOOR_NOT_FOUND, exception.getMessageType());
        verify(floorRepository, times(1)).findById(floorId);
        verify(floorMapper, never()).floorToDtoFloor(any());
    }

    @Test
    @DisplayName("Kat kaydet - Başarılı")
    void saveOneFloor_WhenValid_ShouldSaveAndReturnDtoFloor() {
        // Arrange
        when(buildingService.checkIfBuildingExists(buildingId)).thenReturn(building);
        when(floorMapper.dtoFloorToFloor(dtoFloor)).thenReturn(floor);
        when(floorRepository.save(floor)).thenReturn(floor);
        when(floorMapper.floorToDtoFloor(floor)).thenReturn(dtoFloor);

        // Act
        DtoFloor result = floorService.saveOneFloor(dtoFloor);

        // Assert
        assertNotNull(result);
        assertEquals(floorName, result.getFloorName());
        verify(buildingService, times(1)).checkIfBuildingExists(buildingId);
        verify(floorMapper, times(1)).dtoFloorToFloor(dtoFloor);
        verify(floorRepository, times(1)).save(floor);
        verify(floorMapper, times(1)).floorToDtoFloor(floor);
    }

    @Test
    @DisplayName("Kat kaydet - Kat adı boş")
    void saveOneFloor_WhenFloorNameEmpty_ShouldThrowValidationException() {
        // Arrange
        DtoFloor invalidDtoFloor = new DtoFloor();
        invalidDtoFloor.setFloorName(null);
        invalidDtoFloor.setBuildingId(buildingId);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            floorService.saveOneFloor(invalidDtoFloor);
        });

        assertEquals(MessageType.FLOOR_NAME_REQUIRED.getMessageKey(), exception.getMessage());
        verify(floorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Kat güncelle - Başarılı")
    void updateOneFloor_WhenValid_ShouldUpdateAndReturnDtoFloor() {
        // Arrange
        String updatedFloorName = "Güncellenen Kat";
        
        DtoFloor updatedDtoFloor = new DtoFloor();
        updatedDtoFloor.setFloorName(updatedFloorName);
        updatedDtoFloor.setBuildingId(buildingId);

        Floor updatedFloor = new Floor();
        updatedFloor.setFloorId(floorId);
        updatedFloor.setFloorName(updatedFloorName);
        updatedFloor.setBuilding(building);

        when(floorRepository.findById(floorId)).thenReturn(Optional.of(floor));
        when(buildingService.checkIfBuildingExists(buildingId)).thenReturn(building);
        when(floorRepository.save(floor)).thenReturn(updatedFloor);
        when(floorMapper.floorToDtoFloor(updatedFloor)).thenReturn(updatedDtoFloor);

        // Act
        DtoFloor result = floorService.updateOneFloor(floorId, updatedDtoFloor);

        // Assert
        assertNotNull(result);
        assertEquals(updatedFloorName, result.getFloorName());
        verify(floorRepository, times(1)).findById(floorId);
        verify(buildingService, times(1)).checkIfBuildingExists(buildingId);
        verify(floorRepository, times(1)).save(floor);
        verify(floorMapper, times(1)).floorToDtoFloor(updatedFloor);
    }

    @Test
    @DisplayName("Kat güncelle - Kat mevcut değil")
    void updateOneFloor_WhenFloorDoesNotExist_ShouldThrowException() {
        // Arrange
        when(floorRepository.findById(floorId)).thenReturn(Optional.empty());

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> {
            floorService.updateOneFloor(floorId, dtoFloor);
        });

        assertEquals(MessageType.FLOOR_NOT_FOUND, exception.getMessageType());
        verify(floorRepository, times(1)).findById(floorId);
        verify(floorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Kat sil - Başarılı")
    void deleteOneFloor_WhenFloorExists_ShouldDeleteFloor() {
        // Arrange
        when(floorRepository.findById(floorId)).thenReturn(Optional.of(floor));
        doNothing().when(floorRepository).delete(floor);

        // Act
        floorService.deleteOneFloor(floorId);

        // Assert
        verify(floorRepository, times(1)).findById(floorId);
        verify(floorRepository, times(1)).delete(floor);
    }

    @Test
    @DisplayName("Kat sil - Kat mevcut değil")
    void deleteOneFloor_WhenFloorDoesNotExist_ShouldThrowException() {
        // Arrange
        when(floorRepository.findById(floorId)).thenReturn(Optional.empty());

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> {
            floorService.deleteOneFloor(floorId);
        });

        assertEquals(MessageType.FLOOR_NOT_FOUND, exception.getMessageType());
        verify(floorRepository, times(1)).findById(floorId);
        verify(floorRepository, never()).delete(any());
    }
}
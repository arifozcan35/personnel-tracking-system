package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoBuilding;
import com.personneltrackingsystem.dto.DtoBuildingIU;
import com.personneltrackingsystem.entity.Building;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.BuildingMapper;
import com.personneltrackingsystem.repository.BuildingRepository;
import com.personneltrackingsystem.service.BuildingService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;

    private final BuildingMapper buildingMapper;

    private final MessageResolver messageResolver;

    @Override
    public List<DtoBuilding> getAllBuildings(){

        List<Building> buildingList =  buildingRepository.findAll();

        return buildingMapper.buildingListToDtoBuildingList(buildingList);
    }

    @Override
    public Optional<DtoBuilding> getBuildingById(Long buildingId) {

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new EntityNotFoundException("Building not found with id: " + buildingId));

        return Optional.ofNullable(buildingMapper.buildingToDtoBuilding(building));
    }

    @Override
    public DtoBuilding getOneBuilding(Long buildingId){
        Optional<Building> optBuilding =  buildingRepository.findById(buildingId);
        if(optBuilding.isEmpty()){
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }else{
            return buildingMapper.buildingToDtoBuilding(optBuilding.get());
        }
    }

    @Override
    @Transactional
    public DtoBuilding saveOneBuilding(DtoBuilding building) {


        String buildingName = building.getBuildingName();
        if (ObjectUtils.isEmpty(buildingName)) {
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }

        if (buildingRepository.existsByBuildingName(buildingName)) {
            throw new ValidationException("Building with this building name already exists!");
        }

        Building pBuilding = buildingMapper.dtoBuildingToBuilding(building);
        Building dbBuilding = buildingRepository.save(pBuilding);

        return buildingMapper.buildingToDtoBuilding(dbBuilding);

    }

    @Override
    @Transactional
    public DtoBuilding updateOneBuilding(Long id, DtoBuildingIU newBuilding) {

        Optional<Building> optBuilding = buildingRepository.findById(id);

        if(optBuilding.isPresent()){
            Building foundBuilding = optBuilding.get();
            foundBuilding.setBuildingName(newBuilding.getBuildingName());

            Building updatedBuilding = buildingRepository.save(foundBuilding);

            return buildingMapper.buildingToDtoBuilding(updatedBuilding);
        }else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }

    }

    @Override
    @Transactional
    public void deleteOneBuilding(Long buildingId) {
        Optional<Building> optBuilding = buildingRepository.findById(buildingId);

        if(optBuilding.isPresent()){
     
            buildingRepository.delete(optBuilding.get());
        }
        else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }

} 
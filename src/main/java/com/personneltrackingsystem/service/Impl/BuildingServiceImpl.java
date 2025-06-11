package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoBuilding;
import com.personneltrackingsystem.entity.Building;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.BuildingMapper;
import com.personneltrackingsystem.repository.BuildingRepository;
import com.personneltrackingsystem.service.BuildingService;

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

    @Override
    public Building checkIfBuildingExists(Long buildingId){
        return buildingRepository.findById(buildingId)
            .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BUILDING_NOT_FOUND, buildingId.toString())));
    }


    @Override
    public List<DtoBuilding> getAllBuildings(){

        List<Building> buildingList =  buildingRepository.findAll();

        return buildingMapper.buildingListToDtoBuildingList(buildingList);
    }


    @Override
    public Optional<DtoBuilding> getBuildingById(Long buildingId) {

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BUILDING_NOT_FOUND, buildingId.toString())));

        return Optional.ofNullable(buildingMapper.buildingToDtoBuilding(building));
    }


    @Override
    public DtoBuilding getOneBuilding(Long buildingId){
        Optional<Building> optBuilding =  buildingRepository.findById(buildingId);
        if(optBuilding.isEmpty()){
            throw new BaseException(new ErrorMessage(MessageType.BUILDING_NOT_FOUND, buildingId.toString()));
        }else{
            return buildingMapper.buildingToDtoBuilding(optBuilding.get());
        }
    }


    @Override
    @Transactional
    public DtoBuilding saveOneBuilding(DtoBuilding building) {

        String buildingName = building.getBuildingName();
        if (ObjectUtils.isEmpty(buildingName)) {
            throw new ValidationException(MessageType.BUILDING_NAME_REQUIRED);
        }

        if (buildingRepository.existsByBuildingName(buildingName)) {
            throw new ValidationException(MessageType.BUILDING_NAME_ALREADY_EXISTS, buildingName);
        }

        Building pBuilding = buildingMapper.dtoBuildingToBuilding(building);
        Building dbBuilding = buildingRepository.save(pBuilding);

        return buildingMapper.buildingToDtoBuilding(dbBuilding);

    }


    @Override
    @Transactional
    public DtoBuilding updateOneBuilding(Long id, DtoBuilding newBuilding) {
        Building existingBuilding = buildingRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BUILDING_NOT_FOUND, id.toString())));

        if (ObjectUtils.isNotEmpty(newBuilding.getBuildingName())) {
            if (!existingBuilding.getBuildingName().equals(newBuilding.getBuildingName()) && 
                buildingRepository.existsByBuildingName(newBuilding.getBuildingName())) {
                throw new ValidationException(MessageType.BUILDING_NAME_ALREADY_EXISTS, newBuilding.getBuildingName());
            }
            existingBuilding.setBuildingName(newBuilding.getBuildingName());
        }

        Building updatedBuilding = buildingRepository.save(existingBuilding);
        return buildingMapper.buildingToDtoBuilding(updatedBuilding);
    }


    @Override
    @Transactional
    public void deleteOneBuilding(Long buildingId) {
        Optional<Building> optBuilding = buildingRepository.findById(buildingId);

        if(optBuilding.isPresent()){
            buildingRepository.delete(optBuilding.get());
        }
        else{
            throw new BaseException(new ErrorMessage(MessageType.BUILDING_NOT_FOUND, buildingId.toString()));
        }
    }

} 
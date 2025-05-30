package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoWorkingHours;
import com.personneltrackingsystem.entity.WorkingHours;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.WorkingHoursMapper;
import com.personneltrackingsystem.repository.WorkingHoursRepository;
import com.personneltrackingsystem.service.WorkingHoursService;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkingHoursServiceImpl implements WorkingHoursService {

    private final WorkingHoursRepository workingHoursRepository;

    private final WorkingHoursMapper workingHoursMapper;


    @Override
    public List<DtoWorkingHours> getAllWorkingHours(){

        List<WorkingHours> workingHoursList = workingHoursRepository.findAll();

        return workingHoursMapper.workingHoursListToDtoWorkingHoursList(workingHoursList);
    }


    @Override
    public Optional<DtoWorkingHours> getWorkingHoursById(Long workingHoursId) {

        WorkingHours workingHours = workingHoursRepository.findById(workingHoursId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.WORKING_HOURS_NOT_FOUND, workingHoursId.toString())));

        return Optional.ofNullable(workingHoursMapper.workingHoursToDtoWorkingHours(workingHours));
    }


    @Override
    public DtoWorkingHours getOneWorkingHours(Long workingHoursId){
        Optional<WorkingHours> optWorkingHours =  workingHoursRepository.findById(workingHoursId);
        if(optWorkingHours.isEmpty()){
            throw new BaseException(new ErrorMessage(MessageType.WORKING_HOURS_NOT_FOUND, workingHoursId.toString()));
        }else{
            return workingHoursMapper.workingHoursToDtoWorkingHours(optWorkingHours.get());
        }
    }


    @Override
    @Transactional
    public DtoWorkingHours saveOneWorkingHours(DtoWorkingHours workingHours) {

        LocalTime checkInTime = workingHours.getCheckInTime();
        LocalTime checkOutTime = workingHours.getCheckOutTime();
        Long personelTypeId = workingHours.getPersonelTypeId();
        
        if (ObjectUtils.isEmpty(checkInTime) || ObjectUtils.isEmpty(checkOutTime) || ObjectUtils.isEmpty(personelTypeId)) {
            throw new ValidationException(MessageType.WORKING_HOURS_REQUIRED);
        }

        // Business logic validation: check-in time should be before check-out time
        if (checkInTime.isAfter(checkOutTime)) {
            throw new ValidationException(MessageType.INVALID_TIME_RANGE);
        }

        WorkingHours pWorkingHours = workingHoursMapper.dtoWorkingHoursToWorkingHours(workingHours);
        WorkingHours dbWorkingHours = workingHoursRepository.save(pWorkingHours);

        return workingHoursMapper.workingHoursToDtoWorkingHours(dbWorkingHours);

    }


    @Override
    @Transactional
    public DtoWorkingHours updateOneWorkingHours(Long id, DtoWorkingHours newWorkingHours) {
        WorkingHours existingWorkingHours = workingHoursRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.WORKING_HOURS_NOT_FOUND, id.toString())));

        if (ObjectUtils.isNotEmpty(newWorkingHours.getCheckInTime())) {
            existingWorkingHours.setCheckInTime(newWorkingHours.getCheckInTime());
        }
        
        if (ObjectUtils.isNotEmpty(newWorkingHours.getCheckOutTime())) {
            existingWorkingHours.setCheckOutTime(newWorkingHours.getCheckOutTime());
        }

        // Business logic validation: check-in time should be before check-out time
        if (existingWorkingHours.getCheckInTime().isAfter(existingWorkingHours.getCheckOutTime())) {
            throw new ValidationException(MessageType.INVALID_TIME_RANGE);
        }

        WorkingHours updatedWorkingHours = workingHoursRepository.save(existingWorkingHours);
        return workingHoursMapper.workingHoursToDtoWorkingHours(updatedWorkingHours);
    }

    
    @Override
    @Transactional
    public void deleteOneWorkingHours(Long workingHoursId) {
        Optional<WorkingHours> optWorkingHours = workingHoursRepository.findById(workingHoursId);

        if(optWorkingHours.isPresent()){
            workingHoursRepository.delete(optWorkingHours.get());
        }
        else{
            throw new BaseException(new ErrorMessage(MessageType.WORKING_HOURS_NOT_FOUND, workingHoursId.toString()));
        }
    }
} 
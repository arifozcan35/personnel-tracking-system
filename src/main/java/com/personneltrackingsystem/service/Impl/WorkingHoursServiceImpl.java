package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoPersonelType;
import com.personneltrackingsystem.dto.DtoWorkingHours;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.PersonelType;
import com.personneltrackingsystem.entity.WorkingHours;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.WorkingHoursMapper;
import com.personneltrackingsystem.repository.WorkingHoursRepository;
import com.personneltrackingsystem.service.WorkingHoursService;

import jakarta.persistence.EntityNotFoundException;
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

    private final MessageResolver messageResolver;

    @Override
    public List<DtoWorkingHours> getAllWorkingHours(){

        List<WorkingHours> workingHoursList = workingHoursRepository.findAll();

        return workingHoursMapper.workingHoursListToDtoWorkingHoursList(workingHoursList);
    }

    @Override
    public Optional<DtoWorkingHours> getWorkingHoursById(Long workingHoursId) {

        WorkingHours workingHours = workingHoursRepository.findById(workingHoursId)
                .orElseThrow(() -> new EntityNotFoundException("WorkingHours not found with id: " + workingHoursId));

        return Optional.ofNullable(workingHoursMapper.workingHoursToDtoWorkingHours(workingHours));
    }

    @Override
    public DtoWorkingHours getOneWorkingHours(Long workingHoursId){
        Optional<WorkingHours> optWorkingHours =  workingHoursRepository.findById(workingHoursId);
        if(optWorkingHours.isEmpty()){
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
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
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }


        WorkingHours pWorkingHours = workingHoursMapper.dtoWorkingHoursToWorkingHours(workingHours);
        WorkingHours dbWorkingHours = workingHoursRepository.save(pWorkingHours);

        return workingHoursMapper.workingHoursToDtoWorkingHours(dbWorkingHours);

    }

    @Override
    @Transactional
    public DtoWorkingHours updateOneWorkingHours(Long id, DtoWorkingHours newWorkingHours) {

        Optional<WorkingHours> optWorkingHours = workingHoursRepository.findById(id);

        if(optWorkingHours.isPresent()){
            WorkingHours foundWorkingHours = optWorkingHours.get();
            foundWorkingHours.setCheckInTime(newWorkingHours.getCheckInTime());
            foundWorkingHours.setCheckOutTime(newWorkingHours.getCheckOutTime());

            WorkingHours updatedWorkingHours = workingHoursRepository.save(foundWorkingHours);

            return workingHoursMapper.workingHoursToDtoWorkingHours(updatedWorkingHours);
        }else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }

    }

    @Override
    @Transactional
    public void deleteOneWorkingHours(Long workingHoursId) {
        Optional<WorkingHours> optWorkingHours = workingHoursRepository.findById(workingHoursId);

        if(optWorkingHours.isPresent()){
            workingHoursRepository.delete(optWorkingHours.get());
        }
        else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }
} 
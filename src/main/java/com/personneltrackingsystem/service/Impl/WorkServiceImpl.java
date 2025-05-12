package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.*;
import com.personneltrackingsystem.entity.WorkingHours;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.mapper.WorkMapper;
import com.personneltrackingsystem.repository.WorkRepository;
import com.personneltrackingsystem.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WorkServiceImpl implements WorkService {

    private final WorkRepository workRepository;

    private final WorkMapper workMapper;


    public static final LocalTime WORK_START = LocalTime.of(9, 0);
    public static final LocalTime WORK_FINISH = LocalTime.of(18, 0);
    public static final Duration MAX_WORK_MISSING = Duration.ofMinutes(15);
    public static final double PENALTY_AMOUNT = 200.0;


    @Override
    public Optional<WorkingHours> findById(Long workId) {
        return workRepository.findById(workId);
    }

    @Override
    public WorkingHours save(WorkingHours work) {
        return workRepository.save(work);
    }

    @Override
    public void deleteById(Long workId) {
        workRepository.deleteById(workId);
    }


    @Override
    public List<DtoWorkIU> getAllWorks(){

        List<WorkingHours> unitWork = workRepository.findAll();

        return workMapper.workListToDtoWorkIUList(unitWork);
    }


    @Override
    public DtoWorkIU getOneWork(Long unitId){

        Optional<WorkingHours> optWork =  workRepository.findById(unitId);
        if(optWork.isPresent()){
            return workMapper.workToDtoWorkIU(optWork.get());
        }
        else{
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, unitId.toString()));
        }

    }


    @Override
    @Transactional
    public void deleteOneWork(Long workId) {
        Optional<WorkingHours> optWork = workRepository.findById(workId);

        if (optWork.isPresent()) {
            // make personnel connected to Work null
            workRepository.detachPersonelFromWork(optWork.get());

            // delete work
            workRepository.delete(optWork.get());

        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, workId.toString()));
        }
    }


}

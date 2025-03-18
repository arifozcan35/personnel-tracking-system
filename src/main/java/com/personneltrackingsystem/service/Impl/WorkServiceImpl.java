package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.entity.Work;
import com.personneltrackingsystem.repository.WorkRepository;
import com.personneltrackingsystem.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WorkServiceImpl implements WorkService {

    private final WorkRepository workRepository;

    public static final LocalTime WORK_START = LocalTime.of(9, 0);
    public static final LocalTime WORK_FINISH = LocalTime.of(18, 0);
    public static final Duration MAX_WORK_MISSING = Duration.ofMinutes(15);
    public static final double PENALTY_AMOUNT = 200.0;


    @Override
    public Optional<Work> findById(Long workId) {
        return workRepository.findById(workId);
    }

    @Override
    public Work save(Work work) {
        return workRepository.save(work);
    }

    @Override
    public void deleteById(Long workId) {
        workRepository.deleteById(workId);
    }


}

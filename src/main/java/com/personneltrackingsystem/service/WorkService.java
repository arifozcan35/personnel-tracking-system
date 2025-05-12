package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoWorkIU;
import com.personneltrackingsystem.entity.WorkingHours;

import java.util.List;
import java.util.Optional;

public interface WorkService {

    Optional<WorkingHours> findById(Long workId);

    WorkingHours save(WorkingHours work);

    void deleteById(Long workId);

    List<DtoWorkIU> getAllWorks();

    DtoWorkIU getOneWork(Long workId);

    void deleteOneWork(Long workId);
}

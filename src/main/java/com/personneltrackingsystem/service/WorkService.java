package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoWork;
import com.personneltrackingsystem.dto.DtoWorkIU;
import com.personneltrackingsystem.entity.Work;

import java.util.List;
import java.util.Optional;

public interface WorkService {

    Optional<Work> findById(Long workId);

    Work save(Work work);

    void deleteById(Long workId);

    List<DtoWorkIU> getAllWorks();

    DtoWorkIU getOneWork(Long workId);

    void deleteOneWork(Long workId);
}

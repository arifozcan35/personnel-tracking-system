package com.personneltrackingsystem.service;

import com.personneltrackingsystem.entity.Work;

import java.util.Optional;

public interface WorkService {

    Optional<Work> findById(Long workId);

    Work save(Work work);

    void deleteById(Long workId);

}

package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoWorkingHours;
import com.personneltrackingsystem.dto.DtoWorkingHoursIU;

import java.util.List;
import java.util.Optional;

public interface WorkingHoursService {
    List<DtoWorkingHours> getAllWorkingHours();

    Optional<DtoWorkingHours> getWorkingHoursById(Long id);

    DtoWorkingHours getOneWorkingHours(Long id);

    DtoWorkingHours saveOneWorkingHours(DtoWorkingHours newWorkingHours);

    DtoWorkingHours updateOneWorkingHours(Long id, DtoWorkingHoursIU workingHours);

    void deleteOneWorkingHours(Long id);
} 
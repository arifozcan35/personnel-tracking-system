package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.WorkingHoursController;
import com.personneltrackingsystem.dto.DtoWorkingHours;
import com.personneltrackingsystem.service.WorkingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WorkingHoursControllerImpl implements WorkingHoursController {

    private final WorkingHoursService workingHoursService;

    @Override
    public List<DtoWorkingHours> getAllWorkingHours() {
        return workingHoursService.getAllWorkingHours();
    }

    @Override
    public DtoWorkingHours getOneWorkingHours(Long workingHoursId) {
        return workingHoursService.getOneWorkingHours(workingHoursId);
    }

    @Override
    public DtoWorkingHours createWorkingHours(DtoWorkingHours newWorkingHours) {
        return workingHoursService.saveOneWorkingHours(newWorkingHours);
    }

    @Override
    public DtoWorkingHours updateWorkingHours(Long workingHoursId, DtoWorkingHours newWorkingHours) {
        return workingHoursService.updateOneWorkingHours(workingHoursId, newWorkingHours);
    }

    @Override
    public void deleteWorkingHours(Long workingHoursId) {
        workingHoursService.deleteOneWorkingHours(workingHoursId);
    }
} 
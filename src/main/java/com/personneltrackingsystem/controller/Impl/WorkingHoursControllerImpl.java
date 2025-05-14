package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.WorkingHoursController;
import com.personneltrackingsystem.dto.DtoWork;
import com.personneltrackingsystem.dto.DtoWorkIU;
import com.personneltrackingsystem.service.WorkingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WorkingHoursControllerImpl implements WorkingHoursController {

    private final WorkingHoursService workingHoursService;

    @Override
    public List<DtoWork> getAllWorkingHours() {
        return workingHoursService.getAllWorkingHours();
    }

    @Override
    public DtoWork getOneWorkingHours(Long workingHoursId) {
        return workingHoursService.getOneWorkingHours(workingHoursId);
    }

    @Override
    public DtoWork createWorkingHours(DtoWorkIU newWorkingHours) {
        return workingHoursService.createWorkingHours(newWorkingHours);
    }

    @Override
    public DtoWork updateWorkingHours(Long workingHoursId, DtoWorkIU newWorkingHours) {
        return workingHoursService.updateWorkingHours(workingHoursId, newWorkingHours);
    }

    @Override
    public void deleteWorkingHours(Long workingHoursId) {
        workingHoursService.deleteWorkingHours(workingHoursId);
    }
} 
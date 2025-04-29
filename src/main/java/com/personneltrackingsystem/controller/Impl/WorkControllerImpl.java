package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.WorkController;
import com.personneltrackingsystem.dto.DtoWork;
import com.personneltrackingsystem.dto.DtoWorkIU;
import com.personneltrackingsystem.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WorkControllerImpl implements WorkController {

    private final WorkService workServiceImpl;


    @Override
    public List<DtoWorkIU> getAllWorks() {
        return workServiceImpl.getAllWorks();
    }


    @Override
    public DtoWorkIU getOneWork(Long workId) {
        return workServiceImpl.getOneWork(workId);
    }


    @Override
    public void deleteWork(Long workId) {
        workServiceImpl.deleteOneWork(workId);
    }
}

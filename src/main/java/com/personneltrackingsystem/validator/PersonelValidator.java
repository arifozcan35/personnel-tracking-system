package com.personneltrackingsystem.validator;

import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

import static com.personneltrackingsystem.service.Impl.WorkServiceImpl.*;
import static com.personneltrackingsystem.service.Impl.WorkServiceImpl.PENALTY_AMOUNT;

@Component
@RequiredArgsConstructor
public class PersonelValidator {

    public void validatePersonelForSave(DtoPersonelIU newPersonel, Optional<DtoUnitIU> existingUnit, Optional<DtoGateIU> existingGate) {
        // check unit
        if (ObjectUtils.isEmpty(newPersonel.getUnit()) || ObjectUtils.isEmpty(newPersonel.getUnit().getUnitId())) {
            throw new ValidationException("Could not save personnel! Please enter personnel unit.");
        } else {
            if (existingUnit.isEmpty()) {
                throw new ValidationException("You have not selected a suitable unit!");
            }
        }

        // check gate
        if (ObjectUtils.isEmpty(newPersonel.getGate()) || ObjectUtils.isEmpty(newPersonel.getGate().getGateId())) {
            throw new ValidationException("Personnel registration failed! Please enter the gate.");
        } else {
            if (existingGate.isEmpty()) {
                throw new ValidationException("The specified gate could not be found!");
            }
        }

        // check admin position and salary
        if (ObjectUtils.isEmpty(newPersonel.getAdministrator()) && ObjectUtils.isEmpty(newPersonel.getSalary())) {
            throw new ValidationException("Could not save personnel! At least one of the personnel's manager or salary values must be selected.");
        } else {
            if(!ObjectUtils.isEmpty(newPersonel.getAdministrator())){
                DtoPersonelIU pAdmin = new DtoPersonelIU();
                pAdmin.setAdministrator(selectionPosition(pAdmin, newPersonel.getAdministrator()));
                newPersonel.setSalary(pAdmin.getSalary());
            }else if (ObjectUtils.isEmpty(newPersonel.getAdministrator())){
                DtoPersonelIU pSalary = new DtoPersonelIU();
                salaryAssignment(pSalary, newPersonel.getSalary());
                newPersonel.setAdministrator(pSalary.getAdministrator());
                newPersonel.setSalary(pSalary.getSalary());
            }
        }

        // check working hours
        if (!ObjectUtils.isEmpty(newPersonel.getWork())) {
            LocalTime checkIn = newPersonel.getWork().getCheckInTime();
            LocalTime checkOut = newPersonel.getWork().getCheckOutTime();

            if (ObjectUtils.isEmpty(checkIn) || ObjectUtils.isEmpty(checkOut) || checkOut.isBefore(checkIn)) {
                throw new ValidationException("Invalid check-in/check-out time!");
            }
        }
    }



    public void updatePersonelCheckEntryAndExit(LocalTime entry, LocalTime exit){
        if (ObjectUtils.isEmpty(entry) || ObjectUtils.isEmpty(exit) || !exit.isAfter(entry)) {
            throw new ValidationException("Invalid check-in/check-out time!");
        }
    }



    public Boolean selectionPosition(DtoPersonelIU pAdmin, Boolean administrator){

        if(administrator){
            pAdmin.setSalary(40000.0);
        }
        else{
            pAdmin.setSalary(30000.0);
        }
        return false;
    }


    public void salaryAssignment(DtoPersonelIU pSalary, Double salary){

        if(salary.equals(30000.0) || salary.equals(40000.0)){
            if(salary.equals(40000.0)){
                pSalary.setAdministrator(true);
                pSalary.setSalary(40000.0);
            }else{
                pSalary.setAdministrator(false);
                pSalary.setSalary(30000.0);
            }
        }else{
            if (Math.abs(salary - 30000.0) < Math.abs(salary - 40000.0)) {
                pSalary.setAdministrator(false);
                pSalary.setSalary(30000.0);
            } else {
                pSalary.setAdministrator(true);
                pSalary.setSalary(40000.0);
            }
            throw new ValidationException("The salary can be only 30000.0 or 40000.0! The value you entered is assigned " +
                    "to the value that is closer to these two values (" + pSalary.getSalary() + ")!");
        }
    }



    public Duration calculateWorkTime(LocalTime checkInHour, LocalTime checkOutHour) {
        return Duration.between(checkInHour, checkOutHour);
    }


    public boolean isWorkValid(LocalTime checkInHour, LocalTime checkOutHour) {
        Duration workDuration = calculateWorkTime(checkInHour, checkOutHour);
        Duration workLimit = Duration.between(WORK_START, WORK_FINISH).minus(MAX_WORK_MISSING);

        return workDuration.compareTo(workLimit) >= 0;
    }


    public double calculatePenalty(Duration workPeriod) {
        if (workPeriod.compareTo(Duration.between(WORK_START, WORK_FINISH)) < 0) {
            return PENALTY_AMOUNT;
        }
        return 0.0;
    }
}

package com.personneltrackingsystem.validator;

import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.mapper.UnitMapper;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.GateService;
import com.personneltrackingsystem.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

import static com.personneltrackingsystem.service.Impl.WorkServiceImpl.*;
import static com.personneltrackingsystem.service.Impl.WorkServiceImpl.PENALTY_AMOUNT;

@Component
@RequiredArgsConstructor
public class PersonelValidator {

    private final PersonelRepository personelRepository;

    private final UnitService unitServiceImpl;

    private final GateService gateServiceImpl;

    private final UnitMapper unitMapper;

    private final GateMapper gateMapper;


    public void savePersonelCheckUnit(DtoPersonelIU newPersonel){

        if (newPersonel.getUnit() == null || newPersonel.getUnit().getUnitId() == null) {
            throw new ValidationException("Could not save personnel! Please enter personnel unit.");
        } else {
            Optional<Unit> existingUnit = unitServiceImpl.findById(newPersonel.getUnit().getUnitId());
            if (existingUnit.isEmpty()) {
                throw new ValidationException("You have not selected a suitable unit!");
            } else {
                newPersonel.setUnit(unitMapper.unitToDtoUnit(existingUnit.get()));
            }
        }
    }


    public void savePersonelCheckGate(DtoPersonelIU newPersonel){
        if (newPersonel.getGate() == null || newPersonel.getGate().getGateId() == null) {
            throw new ValidationException("Personnel registration failed! Please enter the gate.");
        } else {
            Optional<Gate> existingGate = gateServiceImpl.findById(newPersonel.getGate().getGateId());
            if (existingGate.isEmpty()) {
                throw new ValidationException("The specified gate could not be found!");
            } else {
                newPersonel.setGate(gateMapper.gateToDtoGate(existingGate.get()));
            }
        }
    }


    public void savePersonelCheckAdminAndSalary(DtoPersonelIU newPersonel){
        if (newPersonel.getAdministrator() == null && newPersonel.getSalary() == null) {
            throw new ValidationException("Could not save personnel! At least one of the personnel's manager or salary values must be selected.");
        } else {
            if(newPersonel.getAdministrator() != null){
                DtoPersonelIU pAdmin = new DtoPersonelIU();
                pAdmin.setAdministrator(selectionPosition(pAdmin, newPersonel.getAdministrator()));
                newPersonel.setSalary(pAdmin.getSalary());
            }else if (newPersonel.getAdministrator() == null){
                DtoPersonelIU pSalary = new DtoPersonelIU();
                salaryAssignment(pSalary, newPersonel.getSalary());
                newPersonel.setAdministrator(pSalary.getAdministrator());
                newPersonel.setSalary(pSalary.getSalary());
            }
        }
    }


    public  void savePersonelCheckEmail(DtoPersonelIU newPersonel){
        Optional<Personel> existingPersonnel = personelRepository.findByEmail(newPersonel.getEmail());
        if (existingPersonnel.isPresent()) {
            throw new ValidationException("Personnel with this email already exists!");
        }
    }


    public void savePersonelCheckWorkingHours(DtoPersonelIU newPersonel){
        LocalTime checkIn = newPersonel.getWork().getCheckInTime();
        LocalTime checkOut = newPersonel.getWork().getCheckOutTime();

        if (checkIn == null || checkOut == null || checkOut.isBefore(checkIn)) {
            throw new ValidationException("Invalid check-in/check-out time!");
        }
    }


    public void updatePersonelCheckUniqueEmail(Long id, DtoPersonelIU newPersonel){
        Optional<Personel> existingEmail = personelRepository.findByEmail(newPersonel.getEmail());
        if (existingEmail.isPresent() && !existingEmail.get().getPersonelId().equals(id)) {
            throw new ValidationException("Email is already in use by another personnel!");
        }
    }


    public  void updatePersonelCheckUnit(Optional<Unit> existingUnit){
        if (existingUnit.isEmpty()) {
            throw new ValidationException("You have not selected a suitable unit!");
        }
    }


    public  void updatePersonelCheckGate(Optional<Gate> existingGate){
        if (existingGate.isEmpty()) {
            throw new ValidationException("The specified gate could not be found!");
        }
    }


    public void updatePersonelCheckEntryAndExit(LocalTime entry, LocalTime exit){
        if (entry == null || exit == null || !exit.isAfter(entry)) {
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

            ///düzeltilecek
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

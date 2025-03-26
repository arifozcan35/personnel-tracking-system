package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.*;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Work;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.mapper.PersonelMapper;
import com.personneltrackingsystem.mapper.UnitMapper;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.GateService;
import com.personneltrackingsystem.service.PersonelService;
import com.personneltrackingsystem.service.UnitService;
import com.personneltrackingsystem.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.personneltrackingsystem.service.Impl.WorkServiceImpl.*;

@RequiredArgsConstructor
@Service
public class PersonelServiceImpl implements PersonelService  {

    private final PersonelRepository personelRepository;

    private final WorkService workServiceImpl;

    private final UnitService unitServiceImpl;

    private final GateService gateServiceImpl;

    private final MessageResolver messageResolver;

    private final PersonelMapper personelMapper;

    private final UnitMapper unitMapper;

    private final GateMapper gateMapper;


    @Override
    public List<DtoPersonel> getAllPersonels() {

        List<Personel> personelList =  personelRepository.findAll();

        return personelMapper.personelsToDtoPersonels(personelList);
    }


    @Override
    public DtoPersonel getAOnePersonel(Long personelId) {
        Optional<Personel> optPersonel =  personelRepository.findById(personelId);

        if(optPersonel.isPresent()){
            return personelMapper.personelToDtoPersonel(optPersonel.get());
        }else{
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, personelId.toString()));
        }
    }


    @Override
    @Transactional
    public ResponseEntity<String> saveOnePersonel(DtoPersonelIU newPersonel) {

        // Validate unit (mandatory field)
        if (newPersonel.getUnit() == null || newPersonel.getUnit().getUnitId() == null) {
            return new ResponseEntity<>("Could not save personnel! Please enter personnel unit.", HttpStatus.BAD_REQUEST);
        } else {
            Optional<Unit> existingUnit = unitServiceImpl.findById(newPersonel.getUnit().getUnitId());
            if (existingUnit.isEmpty()) {
                return new ResponseEntity<>("You have not selected a suitable unit!", HttpStatus.BAD_REQUEST);
            } else {
                newPersonel.setUnit(unitMapper.unitToDtoUnit(existingUnit.get()));
            }
        }

        // Validate gate (mandatory field)
        if (newPersonel.getGate() == null || newPersonel.getGate().getGateId() == null) {
            return new ResponseEntity<>("Personnel registration failed! Please enter the gate.", HttpStatus.BAD_REQUEST);
        } else {
            Optional<Gate> existingGate = gateServiceImpl.findById(newPersonel.getGate().getGateId());
            if (existingGate.isEmpty()) {
                return new ResponseEntity<>("The specified gate could not be found!", HttpStatus.BAD_REQUEST);
            } else {
                newPersonel.setGate(gateMapper.gateToDtoGate(existingGate.get()));
            }
        }

        // Validate salary and administrator
        if (newPersonel.getAdministrator() == null && newPersonel.getSalary() == null) {
            return new ResponseEntity<>("Could not save personnel! At least one of the personnel's manager or salary values must be selected.", HttpStatus.BAD_REQUEST);
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

        // Check for existing personnel with the same email
        Optional<Personel> existingPersonnel = personelRepository.findByEmail(newPersonel.getEmail());
        if (existingPersonnel.isPresent()) {
            return new ResponseEntity<>("Personnel with this email already exists!", HttpStatus.BAD_REQUEST);
        }

        // Convert DTO to Entity
        Personel personelToSave = personelMapper.dtoPersonelIUToPersonel(newPersonel);

        // Handle work hours if provided
        if (newPersonel.getWork() != null) {
            LocalTime checkIn = newPersonel.getWork().getCheckInTime();
            LocalTime checkOut = newPersonel.getWork().getCheckOutTime();

            if (checkIn == null || checkOut == null || checkOut.isBefore(checkIn)) {
                return new ResponseEntity<>("Invalid check-in/check-out time!", HttpStatus.BAD_REQUEST);
            }

            // Calculate and set work hours
            workHoursCalculate2(personelToSave);
            Work savedWork = workServiceImpl.save(newPersonel.getWork());
            personelToSave.setWork(savedWork);
        }

        // Save the personnel
        try {
            Personel savedPersonnel = personelRepository.save(personelToSave);
            return new ResponseEntity<>("Personnel registered successfully with ID: " + savedPersonnel.getPersonelId(), HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Could not save personnel due to a data integrity violation.", HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    @Transactional
    public ResponseEntity<String> updateOnePersonel(Long id, DtoPersonelIU newPersonel) {
        // Find the existing personnel
        Optional<Personel> optPersonel = personelRepository.findById(id);

        if (optPersonel.isEmpty()) {
            return new ResponseEntity<>("No personnel found!", HttpStatus.NOT_FOUND);
        }

        Personel foundPersonel = optPersonel.get();

        // Update name
        if (newPersonel.getName() != null) {
            foundPersonel.setName(newPersonel.getName());
        }

        // Update email with uniqueness check
        if (newPersonel.getEmail() != null) {
            // Check if email is already in use by another personnel
            Optional<Personel> existingEmail = personelRepository.findByEmail(newPersonel.getEmail());
            if (existingEmail.isPresent() && !existingEmail.get().getPersonelId().equals(id)) {
                return new ResponseEntity<>("Email is already in use by another personnel!", HttpStatus.BAD_REQUEST);
            }
            foundPersonel.setEmail(newPersonel.getEmail());
        }

        // Update administrator and salary
        if(newPersonel.getAdministrator() != null){
            foundPersonel.setAdministrator(newPersonel.getAdministrator());

            DtoPersonelIU pAdmin = new DtoPersonelIU();
            pAdmin.setAdministrator(selectionPosition(pAdmin, newPersonel.getAdministrator()));
            foundPersonel.setSalary(pAdmin.getSalary());
        }
        else if(newPersonel.getSalary() != null){
            DtoPersonelIU pSalary = new DtoPersonelIU();
            salaryAssignment(pSalary, newPersonel.getSalary());
            foundPersonel.setAdministrator(pSalary.getAdministrator());
            foundPersonel.setSalary(pSalary.getSalary());
        }

        // Update Unit
        if (newPersonel.getUnit() != null) {
            Optional<Unit> existingUnit = unitServiceImpl.findById(newPersonel.getUnit().getUnitId());
            if (existingUnit.isEmpty()) {
                return new ResponseEntity<>("You have not selected a suitable unit!", HttpStatus.BAD_REQUEST);
            }
            foundPersonel.setUnit(existingUnit.get());
        }

        // Update Gate
        if (newPersonel.getGate() != null) {
            Optional<Gate> existingGate = gateServiceImpl.findById(newPersonel.getGate().getGateId());
            if (existingGate.isEmpty()) {
                return new ResponseEntity<>("The specified gate could not be found!", HttpStatus.BAD_REQUEST);
            }
            foundPersonel.setGate(existingGate.get());
        }

        // Update Working Hours
        if (newPersonel.getWork() != null) {
            LocalTime entry = newPersonel.getWork().getCheckInTime();
            LocalTime exit = newPersonel.getWork().getCheckOutTime();

            // Validate check-in and check-out times
            if (entry == null || exit == null || !exit.isAfter(entry)) {
                return new ResponseEntity<>("Invalid check-in/check-out time!", HttpStatus.BAD_REQUEST);
            }

            // If personnel already has a work record, update the existing one
            Work existingWork = foundPersonel.getWork();
            if (existingWork != null) {
                // Update existing work record
                existingWork.setCheckInTime(entry);
                existingWork.setCheckOutTime(exit);

                // Save the updated work record
                workHoursCalculate2(foundPersonel);
                workServiceImpl.save(existingWork);
            } else {
                // If no existing work record, create a new one
                Work newWork = newPersonel.getWork();
                workHoursCalculate2(foundPersonel);
                Work savedWork = workServiceImpl.save(newWork);
                foundPersonel.setWork(savedWork);
            }
        }

        try {
            // Save the updated personnel
            Personel updatedPersonnel = personelRepository.save(foundPersonel);
            return new ResponseEntity<>("Personnel updated successfully with ID: " + updatedPersonnel.getPersonelId(), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Could not update personnel due to a data integrity violation.", HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    @Transactional
    public void deleteOnePersonel(Long id) {
        Optional<Personel> optPersonel = personelRepository.findById(id);
        if (optPersonel.isPresent()) {
            // Delete the shift record first
            if (optPersonel.get().getWork() != null) {
                workServiceImpl.deleteById(optPersonel.get().getWork().getWorkId());
            }
            // Then delete the personnel record
            personelRepository.deleteById(id);

            System.out.println("The personnel was deleted successfully.");
        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString()));
        }
    }


    @Override
    public DtoPersonel calculateSalaryByPersonelId(Long personelId) {
        return workHoursCalculate(personelId);
    }


    @Override
    public DtoPersonel workHoursCalculate(Long personelId) {
        Optional<Personel> optPersonel = personelRepository.findById(personelId);

        if(optPersonel.isPresent() && optPersonel.get().getWork() != null){
            Work work = optPersonel.get().getWork();
            Double salary = optPersonel.get().getSalary();

            LocalTime checkInHour = work.getCheckInTime();
            LocalTime checkOutHour = work.getCheckOutTime();

            Duration workTime = calculateWorkTime(checkInHour, checkOutHour);

            boolean valid = isWorkValid(checkInHour, checkOutHour);
            optPersonel.get().getWork().setIsWorkValid(valid);

            if (Boolean.TRUE.equals(optPersonel.get().getPersonelId())) {
                optPersonel.get().setSalary(salary);
            } else {
                if (!valid) {
                    double penalty = calculatePenalty(workTime);
                    optPersonel.get().setSalary(optPersonel.get().getSalary() - penalty);
                }
            }

            personelRepository.save(optPersonel.get());

            return new DtoPersonel(optPersonel.get().getName(), optPersonel.get().getEmail(), optPersonel.get().getSalary());

        }else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }


    @Override
    public void workHoursCalculate2(Personel newPersonel) {

        Work work = newPersonel.getWork();

        Double salary = newPersonel.getSalary();

        LocalTime checkInHour = work.getCheckInTime();
        LocalTime checkOutHour = work.getCheckOutTime();

        Duration workTime = calculateWorkTime(checkInHour, checkOutHour);

        boolean valid = isWorkValid(checkInHour, checkOutHour);
        newPersonel.getWork().setIsWorkValid(valid);

        // if employee is an admin then ignore the pay cut :)
        if (newPersonel.getAdministrator() != null && newPersonel.getAdministrator() == true) {
            newPersonel.setSalary(salary);
        } else {
            // if employee is not an admin then
            if (!valid) {
                double penalty = calculatePenalty(workTime);
                newPersonel.setSalary(((newPersonel.getSalary()) - (penalty)));
            }
        }

        // Personel realPersonel = personelMapper.dtoPersonelIUToPersonel(newPersonel);

        Personel dbPersonel = personelRepository.save(newPersonel);

        personelMapper.personelToDtoPersonelIU(dbPersonel);

    }


    @Override
    public Work getOneWorkofPersonel(Long personelId) {
        Optional<Personel> optPersonel = personelRepository.findById(personelId);

        if (optPersonel.isPresent()) {
            Long workId = optPersonel.get().getWork().getWorkId();
            Optional<Work> dbWorkOpt = workServiceImpl.findById(workId);

            if (dbWorkOpt.isPresent()) {
                Work work = new Work();
                BeanUtils.copyProperties(dbWorkOpt.get(), work); // dbWorkOpt.get() (source), work (aim)
                return work;
            } else {
                throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Work with ID: " + workId));
            }
        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, personelId.toString()));
        }
    }


    @Override
    public Map<String, Double> listSalaries(){
        // filling names to list with stream api
        Map<String, Double> salaries = personelRepository.findAll().stream()
                .collect(Collectors.toMap(
                        person -> person.getEmail(),
                        person -> person.getSalary()
                ));
        return salaries;
    }


    protected Duration calculateWorkTime(LocalTime checkInHour, LocalTime checkOutHour) {
        return Duration.between(checkInHour, checkOutHour);
    }


    protected boolean isWorkValid(LocalTime checkInHour, LocalTime checkOutHour) {
        Duration workDuration = calculateWorkTime(checkInHour, checkOutHour);
        Duration workLimit = Duration.between(WORK_START, WORK_FINISH).minus(MAX_WORK_MISSING);
        return workDuration.compareTo(workLimit) >= 0;
    }


    private double calculatePenalty(Duration workPeriod) {
        if (workPeriod.compareTo(Duration.between(WORK_START, WORK_FINISH)) < 0) {
            return PENALTY_AMOUNT;
        }
        return 0.0;
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
            new ResponseEntity<>("The salary can be only 30000.0 or 40000.0! The value you entered is assigned " +
                    "to the value that is closer to these two values (" + pSalary.getSalary() + ")!" , HttpStatus.BAD_REQUEST);
        }
    }
}

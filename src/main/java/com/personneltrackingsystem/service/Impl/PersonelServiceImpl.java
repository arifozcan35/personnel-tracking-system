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
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.GateService;
import com.personneltrackingsystem.service.PersonelService;
import com.personneltrackingsystem.service.UnitService;
import com.personneltrackingsystem.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
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


    @Override
    public List<DtoPersonel> getAllPersonels() {
        List<DtoPersonel> dtoPersonelList = new ArrayList<>();

        List<Personel> personelList =  personelRepository.findAll();
        for (Personel personel : personelList) {
            DtoPersonel dto = new DtoPersonel();
            BeanUtils.copyProperties(personel, dto);
            dtoPersonelList.add(dto);
        }
        return dtoPersonelList;
    }


    @Override
    public DtoPersonel getAOnePersonel(Long personelId) {
        DtoPersonel dto = new DtoPersonel();
        Optional<Personel> optional =  personelRepository.findById(personelId);

        if(optional.isEmpty()){
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, personelId.toString()));
        }else{
            Personel dbPersonel = optional.get();

            BeanUtils.copyProperties(dbPersonel, dto);

            return dto;
        }
    }


    @Override
    public ResponseEntity<String> saveOnePersonel(Personel newPersonel) {

        // Unit control (a mandatory field)
        if (newPersonel.getUnit() == null || newPersonel.getUnit().getUnitId() == null) {
            return new ResponseEntity<>("Could not save personnel! Please enter personnel unit.", HttpStatus.BAD_REQUEST);
        } else {
            Optional<Unit> existingUnit = unitServiceImpl.findById(newPersonel.getUnit().getUnitId());
            if (existingUnit.isEmpty()) {
                return new ResponseEntity<>("You have not selected a suitable unit!", HttpStatus.BAD_REQUEST);
            } else {
                newPersonel.setUnit(existingUnit.get());
            }
        }


        // Gate control (a mandatory field)
        if (newPersonel.getGate() == null || newPersonel.getGate().getGateId() == null) {
            return new ResponseEntity<>("Personnel registration failed! Please enter the gate.", HttpStatus.BAD_REQUEST);
        }else{
            Optional<Gate> existingGate = gateServiceImpl.findById(newPersonel.getGate().getGateId());
            if (existingGate.isEmpty()) {
                return new ResponseEntity<>("The specified gate could not be found!", HttpStatus.BAD_REQUEST);
            }else{
                newPersonel.setGate(existingGate.get());
            }
        }


        // Assign salary value
        if(newPersonel.getAdministrator() == null && newPersonel.getSalary() == null){
            return new ResponseEntity<>("Could not save personnel! At least one of thepersonnel's manager or salary values must be selected.", HttpStatus.BAD_REQUEST);
        }else {
            if(newPersonel.getAdministrator() != null){
                Personel pAdmin = new Personel(newPersonel.getAdministrator());
                newPersonel.setSalary(pAdmin.getSalary());
            }else if (newPersonel.getAdministrator() == null){
                Personel pSalary = new Personel(newPersonel.getSalary());
                newPersonel.setAdministrator(pSalary.getAdministrator());
                newPersonel.setSalary(pSalary.getSalary());
            }
        }

        // saving first
        personelRepository.save(newPersonel);

        // Working hours record
        if (newPersonel.getWork() != null) {
            LocalTime checkIn = newPersonel.getWork().getCheckInTime();
            LocalTime checkOut = newPersonel.getWork().getCheckOutTime();

            if (checkIn == null || checkOut == null || checkOut.isBefore(checkIn)) {
                return new ResponseEntity<>("Invalid check-in/check-out time!", HttpStatus.BAD_REQUEST);
            }

            workHoursCalculate2(newPersonel);
            Work savedWork = workServiceImpl.save(newPersonel.getWork());
            newPersonel.setWork(savedWork);
        }

        return new ResponseEntity<>("Personnel registered successfully!", HttpStatus.CREATED);
    }


    @Override
    public ResponseEntity<String> updateOnePersonel(Long id, Personel newPersonel) {
        Optional<Personel> personel = personelRepository.findById(id);

        if (personel.isPresent()) {
            Personel foundPersonel = personel.get();

            if (newPersonel.getName() != null) {
                foundPersonel.setName(newPersonel.getName());
            }

            if (newPersonel.getEmail() != null) {
                foundPersonel.setEmail(newPersonel.getEmail());
            }

            if(newPersonel.getAdministrator() != null){
                foundPersonel.setAdministrator(newPersonel.getAdministrator());

                Personel pAdmin = new Personel(newPersonel.getAdministrator());
                foundPersonel.setSalary(pAdmin.getSalary());
            }
            else if(newPersonel.getSalary() != null){
                Personel pSalary = new Personel(newPersonel.getSalary());
                foundPersonel.setAdministrator(pSalary.getAdministrator());
                foundPersonel.setSalary(pSalary.getSalary());
            }


            // Unit control
            if (newPersonel.getUnit() != null) {
                Optional<Unit> existingUnit = unitServiceImpl.findById(newPersonel.getUnit().getUnitId());
                if (existingUnit.isEmpty()) {
                    return new ResponseEntity<>("You have not selected a suitable unit!", HttpStatus.BAD_REQUEST);
                }else{
                    foundPersonel.setUnit(existingUnit.get());
                }

            }

            // Gate control
            if (newPersonel.getGate() != null) {
                Optional<Gate> existingGate = gateServiceImpl.findById(newPersonel.getGate().getGateId());

                if (existingGate.isEmpty()) {
                    return new ResponseEntity<>("The specified unit could not be found!", HttpStatus.BAD_REQUEST);
                }else{
                    foundPersonel.setGate(existingGate.get());
                }

            }

            // Working hours record
            if (newPersonel.getWork() != null) {
                LocalTime entry = newPersonel.getWork().getCheckInTime();
                LocalTime checkOut = newPersonel.getWork().getCheckOutTime();

                Long previousRecord = foundPersonel.getWork().getWorkId();

                if (entry != null && checkOut != null && checkOut.isAfter(entry)) {

                    // adding new shift record
                    Work savedWork = workServiceImpl.save(newPersonel.getWork());
                    foundPersonel.setWork(savedWork);

                    // delete previous shift record
                    workServiceImpl.deleteById(previousRecord);
                }
            }

            personelRepository.save(foundPersonel);

            return new ResponseEntity<>("Personnel updated successfully!", HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>("No personnel found!", HttpStatus.NOT_FOUND);
        }

    }


    @Override
    @Transactional
    public void deleteOnePersonel(Long id) {
        Optional<Personel> personel = personelRepository.findById(id);
        if (personel.isPresent()) {
            // Delete the shift record first
            if (personel.get().getWork() != null) {
                workServiceImpl.deleteById(personel.get().getWork().getWorkId());
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
        Personel personel = personelRepository.findById(personelId).orElse(null);

        if (personel == null) {
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }

        Work work = personel.getWork();
        Double salary = personel.getSalary();

        LocalTime checkInHour = work.getCheckInTime();
        LocalTime checkOutHour = work.getCheckOutTime();

        Duration workTime = calculateWorkTime(checkInHour, checkOutHour);

        boolean valid = isWorkValid(checkInHour, checkOutHour);
        personel.getWork().setIsWorkValid(valid);

        if (Boolean.TRUE.equals(personel.getAdministrator())) {
            personel.setSalary(salary);
        } else {
            if (!valid) {
                double penalty = calculatePenalty(workTime);
                personel.setSalary(personel.getSalary() - penalty);
            }
        }

        personelRepository.save(personel);

        return new DtoPersonel(personel.getName(), personel.getEmail(), personel.getSalary());
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

        Personel prsnl = new Personel();
        DtoPersonelIU dtoPrsnl = new DtoPersonelIU();

        BeanUtils.copyProperties(newPersonel, prsnl);


        Personel dbPersonel = personelRepository.save(prsnl);
        BeanUtils.copyProperties(dbPersonel, dtoPrsnl);

    }


    @Override
    public Work getOneWorkofPersonel(Long personelId) {
        Optional<Personel> personel = personelRepository.findById(personelId);

        if (personel.isPresent()) {
            Long workId = personel.get().getWork().getWorkId();
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
                        person -> person.getName(),
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

}

package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.*;
import com.personneltrackingsystem.entity.Work;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.exception.*;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.mapper.PersonelMapper;
import com.personneltrackingsystem.mapper.UnitMapper;
import com.personneltrackingsystem.mapper.WorkMapper;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.GateService;
import com.personneltrackingsystem.service.PersonelService;
import com.personneltrackingsystem.service.UnitService;
import com.personneltrackingsystem.service.WorkService;
import com.personneltrackingsystem.validator.PersonelValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

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

    private final WorkMapper workMapper;

    private final PersonelValidator personelValidator;


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

        // validate all personel
        Optional<DtoUnitIU> prsnlUnitId = unitServiceImpl.findById(newPersonel.getUnit().getUnitId());
        Optional<DtoGateIU> prsnlGateId = gateServiceImpl.findById(newPersonel.getGate().getGateId());
        personelValidator.validatePersonelForSave(newPersonel, prsnlUnitId, prsnlGateId);

        Personel personelToSave = personelMapper.dtoPersonelIUToPersonel(newPersonel);

        // check email
        Optional<Personel> existingPersonnel = personelRepository.findByEmail(newPersonel.getEmail());
        if (existingPersonnel.isPresent()) {
            throw new ValidationException("Personnel with this email already exists!");
        }

        // calculate working hours
        if (!ObjectUtils.isEmpty(newPersonel.getWork())) {
            workHoursCalculate2(personelToSave);
        }

        // after modify cascade type, we save the personel at once
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

        Optional<Personel> optPersonel = personelRepository.findById(id);

        if (optPersonel.isEmpty()) {
            return new ResponseEntity<>("No personnel found!", HttpStatus.NOT_FOUND);
        }

        Personel foundPersonel = optPersonel.get();
        personelMapper.personelToDtoPersonelIU(foundPersonel);

        // update name
        if (!ObjectUtils.isEmpty(newPersonel.getName())) {
            foundPersonel.setName(newPersonel.getName());
        }

        // update email with uniqueness check
        if (!ObjectUtils.isEmpty(newPersonel.getEmail())) {
            // check if email is already in use by another personnel
            Optional<Personel> existingEmail = personelRepository.findByEmail(newPersonel.getEmail());
            if (existingEmail.isPresent() && !existingEmail.get().getPersonelId().equals(id)) {
                throw new ValidationException("Email is already in use by another personnel!");
            }

            foundPersonel.setEmail(newPersonel.getEmail());
        }

        // update administrator position and salary
        if(!ObjectUtils.isEmpty(newPersonel.getAdministrator())){
            foundPersonel.setAdministrator(newPersonel.getAdministrator());

            DtoPersonelIU pAdmin = new DtoPersonelIU();
            pAdmin.setAdministrator(personelValidator.selectionPosition(pAdmin, newPersonel.getAdministrator()));
            foundPersonel.setSalary(pAdmin.getSalary());
        }
        else if(!ObjectUtils.isEmpty(newPersonel.getSalary())){
            DtoPersonelIU pSalary = new DtoPersonelIU();
            personelValidator.salaryAssignment(pSalary, newPersonel.getSalary());
            foundPersonel.setAdministrator(pSalary.getAdministrator());
            foundPersonel.setSalary(pSalary.getSalary());
        }

        // update unit
        if (!ObjectUtils.isEmpty(newPersonel.getUnit())) {
            Optional<DtoUnitIU> existingUnit = unitServiceImpl.findById(newPersonel.getUnit().getUnitId());

            if (existingUnit.isEmpty()) {
                return new ResponseEntity<>("You have not selected a suitable unit!", HttpStatus.NOT_FOUND);
            }

            foundPersonel.setUnit(unitMapper.dtoUnitIUToUnit(existingUnit.get()));
        }

        // update gate
        if (!ObjectUtils.isEmpty(newPersonel.getGate())) {
            Optional<DtoGateIU> existingGate = gateServiceImpl.findById(newPersonel.getGate().getGateId());

            if (existingGate.isEmpty()) {
                return new ResponseEntity<>("The specified gate could not be found!", HttpStatus.NOT_FOUND);
            }

            foundPersonel.setGate(gateMapper.dtoGateIUToGate(existingGate.get()));
        }

        // update working hours
        if (!ObjectUtils.isEmpty(newPersonel.getWork())) {
            LocalTime entry = newPersonel.getWork().getCheckInTime();
            LocalTime exit = newPersonel.getWork().getCheckOutTime();

            // validate check-in and check-out times
            personelValidator.updatePersonelCheckEntryAndExit(entry, exit);

            // if personnel already has a work record, update the existing one
            Work existingWork = foundPersonel.getWork();
            if (!ObjectUtils.isEmpty(existingWork)) {
                // update existing work record
                existingWork.setCheckInTime(entry);
                existingWork.setCheckOutTime(exit);

                // save the updated work record
                workHoursCalculate2(foundPersonel);
                workServiceImpl.save(existingWork);
            } else {
                // if no existing work record, create a new one
                Work newWork = workMapper.dtoWorkToWork(newPersonel.getWork());
                workHoursCalculate2(foundPersonel);
                Work savedWork = workServiceImpl.save(newWork);
                foundPersonel.setWork(savedWork);
            }
        }

        try {
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
            // delete the shift record first
            if (!ObjectUtils.isEmpty(optPersonel.get().getWork())) {
                workServiceImpl.deleteById(optPersonel.get().getWork().getWorkId());
            }

             // Search for orhanRemove, you can handle these two operations in a single code

            // then delete the personnel record
            personelRepository.deleteById(id);

        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString()));
        }
    }



    @Override
    public Set<DtoPersonel> getPersonelsByGateId(Long gateId) {
        Set<DtoPersonel> personels = new HashSet<>();

        Optional<DtoGateIU> optGate = gateServiceImpl.findById(gateId);

        if (optGate.isPresent()) {
            personels.addAll(optGate.get().getPersonels());
        } else {
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }

        return personels;
    }


    @Override
    public Set<DtoPersonel> getPersonelsByUnitId(Long unitId) {
        Set<DtoPersonel> personels = new HashSet<>();

        Optional<DtoUnitIU> optUnit = unitServiceImpl.findById(unitId);

        if (optUnit.isPresent()) {
            personels.addAll(optUnit.get().getPersonels());
        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, unitId.toString()));
        }
        return personels;
    }




    @Override
    public DtoPersonel workHoursCalculate(Long personelId) {
        Optional<Personel> optPersonel = personelRepository.findById(personelId);

        if(!ObjectUtils.isEmpty(optPersonel.isPresent()) && !ObjectUtils.isEmpty(optPersonel.get().getWork())){
            Work work = optPersonel.get().getWork();

            LocalTime checkInHour = work.getCheckInTime();
            LocalTime checkOutHour = work.getCheckOutTime();

            Duration workTime = personelValidator.calculateWorkTime(checkInHour, checkOutHour);

            boolean valid = personelValidator.isWorkValid(checkInHour, checkOutHour);
            optPersonel.get().getWork().setIsWorkValid(valid);

            if (!valid) {
                double penalty = personelValidator.calculatePenalty(workTime);
                optPersonel.get().setSalary(optPersonel.get().getSalary() - penalty);
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

        if(!ObjectUtils.isEmpty(work)){
            LocalTime checkInHour = work.getCheckInTime();
            LocalTime checkOutHour = work.getCheckOutTime();

            Duration workTime = personelValidator.calculateWorkTime(checkInHour, checkOutHour);

            boolean valid = personelValidator.isWorkValid(checkInHour, checkOutHour);
            newPersonel.getWork().setIsWorkValid(valid);

            // if employee is an admin then ignore the pay cut :)
            if (!ObjectUtils.isEmpty(newPersonel.getAdministrator()) && newPersonel.getAdministrator()) {
                newPersonel.setSalary(salary);
            } else {
                // if employee is not an admin then
                if (!valid) {
                    double penalty = personelValidator.calculatePenalty(workTime);
                    newPersonel.setSalary(((newPersonel.getSalary()) - (penalty)));
                }
            }
        }
    }


    @Override
    public Work getOneWorkofPersonel(Long personelId) {
        Optional<Personel> optPersonel = personelRepository.findById(personelId);

        if (optPersonel.isPresent()) {
            Long workId = optPersonel.get().getWork().getWorkId();
            Optional<Work> dbWorkOpt = workServiceImpl.findById(workId);

            if (dbWorkOpt.isPresent()) {
                return personelMapper.DbWorktoWork(dbWorkOpt);
            } else {
                throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Work with ID: " + workId));
            }
        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, personelId.toString()));
        }
    }


    @Override
    public Map<String, Double> listSalaries() {

        List<Personel> allPersonels = personelRepository.findAll();

        return personelMapper.personelsToSalaryMap(allPersonels);
    }

}

package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.*;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Work;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.exception.*;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.mapper.PersonelMapper;
import com.personneltrackingsystem.mapper.UnitMapper;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.GateService;
import com.personneltrackingsystem.service.PersonelService;
import com.personneltrackingsystem.service.UnitService;
import com.personneltrackingsystem.service.WorkService;
import com.personneltrackingsystem.validator.PersonelValidator;
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
        personelValidator.validatePersonelForSave(newPersonel);

        Personel personelToSave = personelMapper.dtoPersonelIUToPersonel(newPersonel);

        // calculate working hours
        if (newPersonel.getWork() != null) {
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

        // update name
        if (newPersonel.getName() != null) {
            foundPersonel.setName(newPersonel.getName());
        }

        // update email with uniqueness check
        if (newPersonel.getEmail() != null) {
            // check if email is already in use by another personnel
            personelValidator.updatePersonelCheckUniqueEmail(id, newPersonel);

            foundPersonel.setEmail(newPersonel.getEmail());
        }

        // update administrator position and salary
        if(newPersonel.getAdministrator() != null){
            foundPersonel.setAdministrator(newPersonel.getAdministrator());

            DtoPersonelIU pAdmin = new DtoPersonelIU();
            pAdmin.setAdministrator(personelValidator.selectionPosition(pAdmin, newPersonel.getAdministrator()));
            foundPersonel.setSalary(pAdmin.getSalary());
        }
        else if(newPersonel.getSalary() != null){
            DtoPersonelIU pSalary = new DtoPersonelIU();
            personelValidator.salaryAssignment(pSalary, newPersonel.getSalary());
            foundPersonel.setAdministrator(pSalary.getAdministrator());
            foundPersonel.setSalary(pSalary.getSalary());
        }

        // update unit
        if (newPersonel.getUnit() != null) {
            Optional<Unit> existingUnit = unitServiceImpl.findById(newPersonel.getUnit().getUnitId());

            personelValidator.updatePersonelCheckUnit(existingUnit);

            foundPersonel.setUnit(existingUnit.get());
        }

        // update gate
        if (newPersonel.getGate() != null) {
            Optional<Gate> existingGate = gateServiceImpl.findById(newPersonel.getGate().getGateId());

            personelValidator.updatePersonelCheckGate(existingGate);

            foundPersonel.setGate(existingGate.get());
        }

        // update working hours
        if (newPersonel.getWork() != null) {
            LocalTime entry = newPersonel.getWork().getCheckInTime();
            LocalTime exit = newPersonel.getWork().getCheckOutTime();

            // validate check-in and check-out times
            personelValidator.updatePersonelCheckEntryAndExit(entry, exit);

            // if personnel already has a work record, update the existing one
            Work existingWork = foundPersonel.getWork();
            if (existingWork != null) {
                // update existing work record
                existingWork.setCheckInTime(entry);
                existingWork.setCheckOutTime(exit);

                // save the updated work record
                workHoursCalculate2(foundPersonel);
                workServiceImpl.save(existingWork);
            } else {
                // if no existing work record, create a new one
                Work newWork = newPersonel.getWork();
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
            if (optPersonel.get().getWork() != null) {
                workServiceImpl.deleteById(optPersonel.get().getWork().getWorkId());
            }
            // then delete the personnel record
            personelRepository.deleteById(id);

        } else {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString()));
        }
    }


    @Override
    public DtoPersonel workHoursCalculate(Long personelId) {
        Optional<Personel> optPersonel = personelRepository.findById(personelId);

        if(optPersonel.isPresent() && optPersonel.get().getWork() != null){
            Work work = optPersonel.get().getWork();
            Double salary = optPersonel.get().getSalary();

            LocalTime checkInHour = work.getCheckInTime();
            LocalTime checkOutHour = work.getCheckOutTime();

            Duration workTime = personelValidator.calculateWorkTime(checkInHour, checkOutHour);

            boolean valid = personelValidator.isWorkValid(checkInHour, checkOutHour);
            optPersonel.get().getWork().setIsWorkValid(valid);

            if (Boolean.TRUE.equals(optPersonel.get().getPersonelId())) {
                optPersonel.get().setSalary(salary);
            } else {
                if (!valid) {
                    double penalty = personelValidator.calculatePenalty(workTime);
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

        if(work != null){
            LocalTime checkInHour = work.getCheckInTime();
            LocalTime checkOutHour = work.getCheckOutTime();

            Duration workTime = personelValidator.calculateWorkTime(checkInHour, checkOutHour);

            boolean valid = personelValidator.isWorkValid(checkInHour, checkOutHour);
            newPersonel.getWork().setIsWorkValid(valid);

            // if employee is an admin then ignore the pay cut :)
            if (newPersonel.getAdministrator() != null && newPersonel.getAdministrator()) {
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

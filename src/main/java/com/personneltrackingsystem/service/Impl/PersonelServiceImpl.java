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
    @Transactional(rollbackFor = NullPointerException.class, readOnly = true)
    public ResponseEntity<String> saveOnePersonel(DtoPersonelIU newPersonel) {

        // Validate unit (mandatory field)
        personelValidator.savePersonelCheckUnit(newPersonel);

        // Validate gate (mandatory field)
        personelValidator.savePersonelCheckGate(newPersonel);

        // Validate salary and administrator
        personelValidator.savePersonelCheckAdminAndSalary(newPersonel);

        // Check for existing personnel with the same email
        personelValidator.savePersonelCheckEmail(newPersonel);

        // Convert DTO to Entity
        Personel personelToSave = personelMapper.dtoPersonelIUToPersonel(newPersonel);

        // Handle work hours if provided
        if (newPersonel.getWork() != null) {
            personelValidator.savePersonelCheckWorkingHours(newPersonel);

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
            personelValidator.updatePersonelCheckUniqueEmail(id, newPersonel);

            foundPersonel.setEmail(newPersonel.getEmail());
        }

        // Update administrator and salary
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

        // Update Unit
        if (newPersonel.getUnit() != null) {
            Optional<Unit> existingUnit = unitServiceImpl.findById(newPersonel.getUnit().getUnitId());

            personelValidator.updatePersonelCheckUnit(existingUnit);

            foundPersonel.setUnit(existingUnit.get());
        }

        // Update Gate
        if (newPersonel.getGate() != null) {
            Optional<Gate> existingGate = gateServiceImpl.findById(newPersonel.getGate().getGateId());

            personelValidator.updatePersonelCheckGate(existingGate);

            foundPersonel.setGate(existingGate.get());
        }

        // Update Working Hours
        if (newPersonel.getWork() != null) {
            LocalTime entry = newPersonel.getWork().getCheckInTime();
            LocalTime exit = newPersonel.getWork().getCheckOutTime();

            // Validate check-in and check-out times
            personelValidator.updatePersonelCheckEntryAndExit(entry, exit);

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


    // silinecek
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


    ///method void olacak ise method güncellensin return değeri olacak ise ona göre güncellenyelim
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

            // Personel realPersonel = personelMapper.dtoPersonelIUToPersonel(newPersonel);

            Personel dbPersonel = personelRepository.save(newPersonel);

            personelMapper.personelToDtoPersonelIU(dbPersonel);
        }

    }


    // beanutils yerine mapper kullan
    @Override
    public Work getOneWorkofPersonel(Long personelId) {
        Optional<Personel> optPersonel = personelRepository.findById(personelId);

        if (optPersonel.isPresent()) {
            Long workId = optPersonel.get().getWork().getWorkId();
            Optional<Work> dbWorkOpt = workServiceImpl.findById(workId);

            //mapper kullanalım
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


    //method mapperda olsun
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

}

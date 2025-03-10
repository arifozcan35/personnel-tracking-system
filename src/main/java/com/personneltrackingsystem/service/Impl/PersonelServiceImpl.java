package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.*;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Work;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.repository.UnitRepository;
import com.personneltrackingsystem.repository.GateRepository;
import com.personneltrackingsystem.repository.WorkRepository;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.IPersonelService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonelServiceImpl implements IPersonelService {

    private final PersonelRepository personelRepository;


    private final WorkRepository workRepository;


    private final WorkServiceImpl workServiceImpl;


    private final UnitRepository unitRepository;


    private final GateRepository gateRepository;

    @Autowired
    public PersonelServiceImpl(PersonelRepository personelRepository, WorkRepository workRepository,
                               WorkServiceImpl workServiceImpl, UnitRepository unitRepository, GateRepository gateRepository) {
        this.personelRepository = personelRepository;
        this.workRepository = workRepository;
        this.workServiceImpl = workServiceImpl;
        this.unitRepository = unitRepository;
        this.gateRepository = gateRepository;
    }


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
        if(optional.isPresent()){
            Personel dbPersonel = optional.get();

            BeanUtils.copyProperties(dbPersonel, dto);
        }
        return dto;
    }



    // codes following are about to pass the dto version - trial
    /*
    @Override
    public ResponseEntity<String> saveOnePersonel(DtoPersonelIU newPersonel) {

        // Unit control (a mandatory field)
        if (newPersonel.getUnit() != null && newPersonel.getUnit().getUnitId() != null) {
            Optional<Unit> existingUnit = unitRepository.findById(newPersonel.getUnit().getUnitId());

            if(existingUnit.isPresent()){
                DtoPersonelIU dtoPrsnl = new DtoPersonelIU();
                BeanUtils.copyProperties(existingUnit, dtoPrsnl.getUnit());

                if (dtoPrsnl.getUnit() == null) {
                    return new ResponseEntity<>("You have not selected a suitable unit!", HttpStatus.BAD_REQUEST);
                }

                newPersonel.setUnit(dtoPrsnl.getUnit());
            }




        } else {
            return new ResponseEntity<>("Could not save personnel! Please enter personnel unit.", HttpStatus.BAD_REQUEST);
        }


        // Gate control (a mandatory field)
        if (newPersonel.getGate() != null && newPersonel.getGate().getGateId() != null) {
            Optional<Gate> existingGate = gateRepository.findById(newPersonel.getGate().getGateId());
            if(existingGate.isPresent()){
                DtoPersonelIU dtoPrsnl = new DtoPersonelIU();
                BeanUtils.copyProperties(existingGate, dtoPrsnl.getGate());

                if (dtoPrsnl.getGate() == null) {
                    return new ResponseEntity<>("The specified unit could not be found!", HttpStatus.BAD_REQUEST);
                }

                newPersonel.setGate(dtoPrsnl.getGate());
            }

        }else {
            return new ResponseEntity<>("Personnel registration failed! Please enter the staff ticket office.", HttpStatus.BAD_REQUEST);
        }


        // Assign salary value
        if(newPersonel.getAdministrator() == null && newPersonel.getSalary() == null){
            return new ResponseEntity<>("Could not save personnel! At least one of thepersonnel's manager or salary values must be selected.", HttpStatus.BAD_REQUEST);
        }else {
            if(newPersonel.getAdministrator() != null){
                DtoPersonelIU pAdmin = new DtoPersonelIU(newPersonel.getAdministrator());
                newPersonel.setSalary(pAdmin.getSalary());
            }else if (newPersonel.getAdministrator() == null){
                DtoPersonelIU pSalary = new DtoPersonelIU(newPersonel.getSalary());
                newPersonel.setAdministrator(pSalary.getAdministrator());
                newPersonel.setSalary(pSalary.getSalary());
            }
        }


        Personel prsnl = new Personel();
        DtoPersonelIU dtoPrsnl = new DtoPersonelIU();

        BeanUtils.copyProperties(newPersonel, prsnl);

        Personel dbPersonel = personelRepository.save(prsnl);
        BeanUtils.copyProperties(dbPersonel, dtoPrsnl);

        // Personel savingPersonel = personelRepository.save(newPersonel);


        // Working hours record
        if (newPersonel.getWork() != null) {
            LocalTime checkIn = newPersonel.getWork().getCheckInTime();
            LocalTime checkOut = newPersonel.getWork().getCheckOutTime();


            // boolean isWorkValid = workService.isWorkValid(checkIn, checkIn);

            // newPersonel.getWork().setIsWorkValid(isWorkValid);



            // Work work = newPersonel.getWork();
            // work.setIsWorkValid(isWorkValid);



            if (checkIn == null && checkOut == null && checkOut.isBefore(checkIn)) {
                return new ResponseEntity<>("Invalid check-in/check-out time!", HttpStatus.BAD_REQUEST);
            } else {
                workServiceImpl.workHoursCalculate2(newPersonel);

                Work savedWork = workRepository.save(newPersonel.getWork());
                newPersonel.setWork(savedWork);
            }
        }

        return new ResponseEntity<>("Personnel registered successfully!", HttpStatus.CREATED);

    }

    */


    @Override
    public ResponseEntity<String> saveOnePersonel(Personel newPersonel) {

        // Unit control (a mandatory field)
        if (newPersonel.getUnit() != null && newPersonel.getUnit().getUnitId() != null) {
            Unit existingUnit = unitRepository.findById(newPersonel.getUnit().getUnitId())
                    .orElse(null);
            if (existingUnit == null) {
                return new ResponseEntity<>("You have not selected a suitable unit!", HttpStatus.BAD_REQUEST);
            }
            newPersonel.setUnit(existingUnit);
        } else {
            return new ResponseEntity<>("Could not save personnel! Please enter personnel unit.", HttpStatus.BAD_REQUEST);
        }


        // Gate control (a mandatory field)
        if (newPersonel.getGate() != null && newPersonel.getGate().getGateId() != null) {
            Gate existingGate = gateRepository.findById(newPersonel.getGate().getGateId())
                    .orElse(null);
            if (existingGate == null) {
                return new ResponseEntity<>("The specified unit could not be found!", HttpStatus.BAD_REQUEST);
            }
            newPersonel.setGate(existingGate);
        }else {
            return new ResponseEntity<>("Personnel registration failed! Please enter the staff ticket office.", HttpStatus.BAD_REQUEST);
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


        personelRepository.save(newPersonel);
        // Personel savingPersonel = personelRepository.save(newPersonel);


        // Working hours record
        if (newPersonel.getWork() != null) {
            LocalTime checkIn = newPersonel.getWork().getCheckInTime();
            LocalTime checkOut = newPersonel.getWork().getCheckOutTime();


            // boolean isWorkValid = workService.isWorkValid(checkIn, checkIn);

            // newPersonel.getWork().setIsWorkValid(isWorkValid);


            /*
            Work work = newPersonel.getWork();
            work.setIsWorkValid(isWorkValid);
            */


            if (checkIn == null && checkOut == null && checkOut.isBefore(checkIn)) {
                return new ResponseEntity<>("Invalid check-in/check-out time!", HttpStatus.BAD_REQUEST);
            } else {
                workServiceImpl.workHoursCalculate2(newPersonel);

                Work savedWork = workRepository.save(newPersonel.getWork());
                newPersonel.setWork(savedWork);
            }
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
                Unit existingUnit = unitRepository.findById(newPersonel.getUnit().getUnitId())
                        .orElse(null);
                if (existingUnit == null) {
                    return new ResponseEntity<>("You have not selected a suitable unit!", HttpStatus.BAD_REQUEST);
                }
                foundPersonel.setUnit(existingUnit);
            }


            // Gate control
            if (newPersonel.getGate() != null) {
                Gate existingGate = gateRepository.findById(newPersonel.getGate().getGateId())
                        .orElse(null);
                if (existingGate == null) {
                    return new ResponseEntity<>("The specified unit could not be found!", HttpStatus.BAD_REQUEST);
                }
                foundPersonel.setGate(existingGate);
            }

            // Working hours record
            if (newPersonel.getWork() != null) {
                LocalTime giris = newPersonel.getWork().getCheckInTime();
                LocalTime cikis = newPersonel.getWork().getCheckOutTime();

                Long oncekiKayit = foundPersonel.getWork().getWorkId();

                if (giris != null && cikis != null && cikis.isAfter(giris)) {

                    // adding new shift record
                    Work kaydedilenWork = workRepository.save(newPersonel.getWork());
                    foundPersonel.setWork(kaydedilenWork);

                    // delete previous shift record
                    workRepository.deleteById(oncekiKayit);
                }
            }

            personelRepository.save(foundPersonel);

            return new ResponseEntity<>("Personnel updated successfully!", HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>("No personnel found!", HttpStatus.NOT_FOUND);
        }

    }


    @Override
    public void deleteOnePersonel(Long id) {
        Optional<Personel> personel = personelRepository.findById(id);
        if (personel.isPresent()) {
            // Delete the shift record first
            if (personel.get().getWork() != null) {
                workRepository.deleteById(personel.get().getWork().getWorkId());
            }
            // Then delete the personnel record
            personelRepository.deleteById(id);
            System.out.println("The personnel was deleted successfully.");
        } else {
            System.out.println("No personnel found.");
        }
    }


    @Override
    public void workHoursCalculate(Long personelId) {
        workServiceImpl.workHoursCalculate(personelId);
    }



}

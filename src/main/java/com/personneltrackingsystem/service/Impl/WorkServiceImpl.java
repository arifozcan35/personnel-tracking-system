package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.entity.Work;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.repository.WorkRepository;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WorkServiceImpl implements WorkService {

    private final WorkRepository workRepository;

    private final PersonelRepository personelRepository;

    private final MessageResolver messageResolver;

    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_FINISH = LocalTime.of(18, 0);
    private static final Duration MAX_WORK_MISSING = Duration.ofMinutes(15);
    private static final double PENALTY_AMOUNT = 200.0;


    @Override
    public Work getOneWorkofPersonel(Long personelId) {
        Optional<Personel> personel = personelRepository.findById(personelId);

        if (personel.isPresent()) {
            Long workId = personel.get().getWork().getWorkId();
            Optional<Work> dbWorkOpt = workRepository.findById(workId);

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


    /*
    @Override
    public void workHoursCalculate(Long personelId) {
        Personel personel = personelRepository.findById(personelId).orElse(null);
        Work work = personel.getWork();

        Double salary = personel.getSalary();

        LocalTime checkInHour = work.getCheckInTime();
        LocalTime checkOutHour = work.getCheckOutTime();

        Duration workTime = calculateWorkTime(checkInHour, checkOutHour);

        boolean valid = isWorkValid(checkInHour, checkOutHour);
        personel.getWork().setIsWorkValid(valid);

            if (personel.getAdministrator() != null && personel.getAdministrator() == true) {
                personel.setSalary(salary);
            } else {
                // personel.setSalary(salary);
                if (!valid) {
                    double penalty = calculatePenalty(workTime);
                    personel.setSalary(((personel.getSalary()) - (penalty)));
                }
            }

        personelRepository.save(personel);
        // return personelRepository.save(personel);
    }
     */


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

        // return personelRepository.save(personel);
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

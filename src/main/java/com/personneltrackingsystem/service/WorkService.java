package com.personneltrackingsystem.service;

import com.personneltrackingsystem.entity.Work;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.repository.WorkRepository;
import com.personneltrackingsystem.repository.PersonelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;

@Service
public class WorkService {
    @Autowired
    private WorkRepository workRepository;
    @Autowired
    private PersonelRepository personelRepository;

    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_FINISH = LocalTime.of(18, 0);
    private static final Duration MAX_WORK_MISSING = Duration.ofMinutes(15);
    private static final double PENALTY_AMOUNT = 200.0;

    public Work getOneWorkofPersonel(Long personelId){
        Personel personel = new Personel();
        personel.setPersonelId(personelId);
        return workRepository.findById(personelId).orElse(null);
    }

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

        personelRepository.save(newPersonel);
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

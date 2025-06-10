package com.personneltrackingsystem.repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Salary;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    
    List<Salary> findByPersonelId(Personel personel);
    
    Optional<Salary> findByPersonelIdAndSalaryMonth(Personel personel, YearMonth month);
    
    List<Salary> findBySalaryMonth(YearMonth month);
    
    List<Salary> findByIsPaid(Boolean isPaid);
} 
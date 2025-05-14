package com.personneltrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.WorkingHours;

@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {

    @Modifying
    @Query("UPDATE PersonelType p SET p.workingHours = NULL WHERE p.workingHours = :workingHours")
    void updatePersonelTypeWorkingHoursReferences(@Param("workingHours") WorkingHours workingHours);

    boolean existsByWorkingHoursId(Long existingWorkingHoursID);
}

package com.personneltrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.Building;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    boolean existsByBuildingName(String existingBuildingName);

    boolean existsById(Long existingBuildingID);
}

package com.personneltrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.Building;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    @Modifying
    @Query("UPDATE Personel p SET p.building = NULL WHERE p.building = :building")
    void updatePersonelBuildingReferences(@Param("building") Building building);

    boolean existsByBuildingName(String existingBuildingName);

    boolean existsById(Long existingBuildingID);
}

package com.personneltrackingsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.Floor;
import com.personneltrackingsystem.entity.Personel;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

    @Query("SELECT p FROM Personel p WHERE p.personelId = :personelId")
    Optional<Personel> findPrsnlById(@Param("personelId") Long personelId);

    @Modifying
    @Query("UPDATE Building b SET b.floor = NULL WHERE b.floor = :floor")
    void updateBuildingFloorReferences(@Param("floor") Floor floor);

    boolean existsByFloorName(String existingFloorName);

    boolean existsByFloorId(Long existingFloorID);
}

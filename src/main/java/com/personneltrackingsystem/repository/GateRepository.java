package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GateRepository extends JpaRepository<Gate, Long> {

    @Query("SELECT p FROM Personel p WHERE p.personelId = :personelId")
    Optional<Personel> findPrsnlById(@Param("personelId") Long personelId);

    @Modifying
    @Query("UPDATE Unit u SET u.gate = NULL WHERE u.gate = :gate")
    void updateUnitGateReferences(@Param("gate") Gate gate);

    boolean existsByGateName(String existingGateName);

    boolean existsById(Long existingGateID);

}

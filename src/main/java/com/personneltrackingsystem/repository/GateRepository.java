package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Gate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GateRepository extends JpaRepository<Gate, Long> {

    boolean existsByGateName(String existingGateName);

    boolean existsById(Long existingGateID);

}

package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    boolean existsByUnitName(String existingUnitName);
}

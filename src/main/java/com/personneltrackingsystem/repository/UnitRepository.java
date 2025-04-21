package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    @Modifying
    @Query("UPDATE Personel p SET p.unit = NULL WHERE p.unit = :unit")
    void detachPersonelFromUnit(@Param("unit") Unit unit);

    boolean existsByUnitName(String existingUnitName);

    boolean existsByUnitId(Long existingUnitID);
}

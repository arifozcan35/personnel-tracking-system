package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Personel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonelRepository extends JpaRepository<Personel, Long> {

    Optional<Personel> findByEmail(String email);
    
    @Query("SELECT p FROM Personel p LEFT JOIN FETCH p.unitId LEFT JOIN FETCH p.personelTypeId WHERE p.personelId = :personelId")
    Optional<Personel> findByIdWithRelationships(@Param("personelId") Long personelId);
}

package com.personneltrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.PersonelType;

@Repository
public interface PersonelTypeRepository extends JpaRepository<PersonelType, Long> {

    @Modifying
    @Query("UPDATE Personel p SET p.personelType = NULL WHERE p.personelType = :personelType")
    void updatePersonelPersonelTypeReferences(@Param("personelType") PersonelType personelType);

    boolean existsByPersonelTypeName(String existingPersonelTypeName);

    boolean existsById(Long existingPersonelTypeID);
}

package com.personneltrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.PersonelType;

@Repository
public interface PersonelTypeRepository extends JpaRepository<PersonelType, Long> {

}

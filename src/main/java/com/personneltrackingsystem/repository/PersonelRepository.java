package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Personel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonelRepository extends JpaRepository<Personel, Long> {

    Optional<Personel> findByEmail(String email);

}

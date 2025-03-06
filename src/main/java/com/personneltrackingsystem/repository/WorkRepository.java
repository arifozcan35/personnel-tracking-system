package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    //List<Mesai> findByPersonel(Personel personel);
}

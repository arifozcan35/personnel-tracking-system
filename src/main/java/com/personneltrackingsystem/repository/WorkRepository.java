package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    @Modifying
    @Query("UPDATE Personel p SET p.work = NULL WHERE p.work = :work")
    void detachPersonelFromWork(@Param("work") Work work);

}

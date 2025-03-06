package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface PersonelRepository extends JpaRepository<Personel, Long> {
    List<Personel> findByUnit(Unit unit);
    List<Personel> findByGate(Gate gate);

    /*
    @Query(value = "SELECT * FROM dbpersonel WHERE ID = ?1", nativeQuery = true)
    User getUserByUserID(String userId);
     */

}

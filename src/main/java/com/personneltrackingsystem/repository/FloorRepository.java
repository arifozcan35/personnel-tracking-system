package com.personneltrackingsystem.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.Floor;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

}

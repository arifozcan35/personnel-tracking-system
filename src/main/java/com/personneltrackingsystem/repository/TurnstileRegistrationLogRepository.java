package com.personneltrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.TurnstileRegistrationLog;

@Repository
public interface TurnstileRegistrationLogRepository extends JpaRepository<TurnstileRegistrationLog, Long> {

}

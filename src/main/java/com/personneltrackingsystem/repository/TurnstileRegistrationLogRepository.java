package com.personneltrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.TurnstileRegistrationLog;

@Repository
public interface TurnstileRegistrationLogRepository extends JpaRepository<TurnstileRegistrationLog, Long> {

    @Query("SELECT COUNT(t) > 0 FROM TurnstileRegistrationLog t WHERE t.personelId.personelId = :personelId AND t.turnstileId.turnstileId = :turnstileId")
    boolean passedTurnstile(@Param("personelId") Long personelId, @Param("turnstileId") Long turnstileId);
}

package com.personneltrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.TurnstileRegistrationLog;

@Repository
public interface TurnstileRegistrationLogRepository extends JpaRepository<TurnstileRegistrationLog, Long> {

    @Modifying
    @Query("UPDATE Turnstile t SET t.turnstileRegistrationLog = NULL WHERE t.turnstileRegistrationLog = :turnstileRegistrationLog")
    void updateTurnstileTurnstileRegistrationLogReferences(@Param("turnstileRegistrationLog") TurnstileRegistrationLog turnstileRegistrationLog);

    @Modifying
    @Query("UPDATE Personel p SET p.turnstileRegistrationLog = NULL WHERE p.turnstileRegistrationLog = :turnstileRegistrationLog")
    void updatePersonelTurnstileRegistrationLogReferences(@Param("turnstileRegistrationLog") TurnstileRegistrationLog turnstileRegistrationLog);

    boolean existsByLogName(String existingLogName);

    boolean existsByLogId(Long existingLogID);
}

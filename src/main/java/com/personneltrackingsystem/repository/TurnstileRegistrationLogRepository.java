package com.personneltrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.TurnstileRegistrationLog;

@Repository
public interface TurnstileRegistrationLogRepository extends JpaRepository<TurnstileRegistrationLog, Long> {

    @Query("SELECT CAST(t.operationType AS string) FROM TurnstileRegistrationLog t WHERE t.personelId.personelId = :personelId AND t.turnstileId.turnstileId = :turnstileId ORDER BY t.operationTime DESC")
    java.util.List<String> findOperationTypesByPersonelAndTurnstile(@Param("personelId") Long personelId, @Param("turnstileId") Long turnstileId);

}

package com.personneltrackingsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.TurnstileRegistrationLog;

@Repository
public interface TurnstileRegistrationLogRepository extends JpaRepository<TurnstileRegistrationLog, Long> {

    @Query("SELECT CAST(t.operationType AS string) FROM TurnstileRegistrationLog t WHERE t.personelId.personelId = :personelId AND t.turnstileId.turnstileId = :turnstileId ORDER BY t.operationTime DESC")
    java.util.List<String> findOperationTypesByPersonelAndTurnstile(@Param("personelId") Long personelId, @Param("turnstileId") Long turnstileId);

    @Query(value = "SELECT COUNT(DISTINCT DATE(trl.operation_time)) FROM dbpersonel.turnstile_registration_log trl " +
           "JOIN dbpersonel.turnstile t ON t.turnstile_id = trl.fk_turnstile_id " +
           "JOIN dbpersonel.gate g ON g.gate_id = t.fk_gate_id " +
           "WHERE trl.fk_personel_id = :personelId " +
           "AND trl.operation_type = :operationType " +
           "AND g.main_entrance = true " +
           "AND (EXTRACT(HOUR FROM trl.operation_time) * 60 + EXTRACT(MINUTE FROM trl.operation_time)) > :lateMinutesThreshold " +
           "AND EXTRACT(YEAR FROM trl.operation_time) = :year " +
           "AND EXTRACT(MONTH FROM trl.operation_time) = :month", nativeQuery = true)
    Integer countLateDaysInMonth(
           @Param("personelId") Long personelId, 
           @Param("operationType") String operationType,
           @Param("lateMinutesThreshold") Integer lateMinutesThreshold,
           @Param("year") Integer year,
           @Param("month") Integer month);
    
    @Query(value = "SELECT trl.* FROM dbpersonel.turnstile_registration_log trl " +
           "JOIN dbpersonel.turnstile t ON t.turnstile_id = trl.fk_turnstile_id " +
           "JOIN dbpersonel.gate g ON g.gate_id = t.fk_gate_id " +
           "WHERE trl.fk_personel_id = :personelId " +
           "AND trl.operation_type = :operationType " +
           "AND g.main_entrance = true " +
           "AND (EXTRACT(HOUR FROM trl.operation_time) * 60 + EXTRACT(MINUTE FROM trl.operation_time)) > :lateMinutesThreshold " +
           "AND EXTRACT(YEAR FROM trl.operation_time) = :year " +
           "AND EXTRACT(MONTH FROM trl.operation_time) = :month " +
           "ORDER BY trl.operation_time", nativeQuery = true)
    List<TurnstileRegistrationLog> findLateTurnstileLogsInMonth(
           @Param("personelId") Long personelId, 
           @Param("operationType") String operationType,
           @Param("lateMinutesThreshold") Integer lateMinutesThreshold,
           @Param("year") Integer year,
           @Param("month") Integer month);
}

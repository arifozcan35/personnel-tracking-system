package com.personneltrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.TurnstileRegistrationLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TurnstileRegistrationLogRepository extends JpaRepository<TurnstileRegistrationLog, Long> {

    @Query("SELECT COUNT(t) > 0 FROM TurnstileRegistrationLog t WHERE t.personelId.personelId = :personelId AND t.turnstileId.turnstileId = :turnstileId")
    boolean passedTurnstile(@Param("personelId") Long personelId, @Param("turnstileId") Long turnstileId);
    
    @Query("SELECT CAST(t.operationType AS string) FROM TurnstileRegistrationLog t WHERE t.personelId.personelId = :personelId AND t.turnstileId.turnstileId = :turnstileId ORDER BY t.operationTime DESC")
    java.util.List<String> findOperationTypesByPersonelAndTurnstile(@Param("personelId") Long personelId, @Param("turnstileId") Long turnstileId);

    @Query("SELECT DISTINCT t.personelId.personelId FROM TurnstileRegistrationLog t WHERE DATE(t.operationTime) = DATE(:date)")
    List<Long> findDistinctPersonelIdsByDate(@Param("date") LocalDateTime date);

    @Query("SELECT t FROM TurnstileRegistrationLog t WHERE t.personelId.personelId = :personelId AND DATE(t.operationTime) = DATE(:date) ORDER BY t.operationTime ASC")
    List<TurnstileRegistrationLog> findByPersonelIdAndDate(@Param("personelId") Long personelId, @Param("date") LocalDateTime date);

    @Query("SELECT t FROM TurnstileRegistrationLog t WHERE DATE(t.operationTime) = DATE(:date) ORDER BY t.personelId.personelId, t.operationTime ASC")
    List<TurnstileRegistrationLog> findAllByDate(@Param("date") LocalDateTime date);
    
    @Query("SELECT t FROM TurnstileRegistrationLog t " +
           "JOIN t.turnstileId tur " +
           "JOIN tur.gateId g " +
           "WHERE YEAR(t.operationTime) = :year AND MONTH(t.operationTime) = :month " +
           "AND g.mainEntrance = true " +
           "ORDER BY t.personelId.personelId, t.operationTime ASC")
    List<TurnstileRegistrationLog> findAllMainEntranceLogsByMonth(@Param("year") int year, @Param("month") int month);
}

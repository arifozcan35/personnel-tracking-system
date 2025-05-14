package com.personneltrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.Turnstile;

@Repository
public interface TurnstileRepository extends JpaRepository<Turnstile, Long> {

    @Modifying
    @Query("UPDATE Gate g SET g.turnstile = NULL WHERE g.turnstile = :turnstile")
    void updateGateTurnstileReferences(@Param("turnstile") Turnstile turnstile);

    boolean existsByTurnstileName(String existingTurnstileName);

    boolean existsById(Long existingTurnstileID);
}

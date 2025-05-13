package com.personneltrackingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.personneltrackingsystem.entity.Turnstile;

@Repository
public interface TurnstileRepository extends JpaRepository<Turnstile, Long> {

}

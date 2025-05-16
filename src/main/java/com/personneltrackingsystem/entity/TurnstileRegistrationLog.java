package com.personneltrackingsystem.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "turnstile_registration_log", schema = "dbpersonel")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class TurnstileRegistrationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "turnstileRegistrationLogSeq")
    @SequenceGenerator(name = "turnstileRegistrationLogSeq", allocationSize = 1)
    @Column(name = "turnstile_registration_log_id")
    private Long turnstileRegistrationLogId;

    @ManyToOne
    @JoinColumn(name = "fk_personel_id")
    private Personel personelId;

    @ManyToOne
    @JoinColumn(name = "fk_turnstile_id")
    private Turnstile turnstileId;

    private LocalDateTime operationTime;

    // entry or exit
    private String operationType;
    
}

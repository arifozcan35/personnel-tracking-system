package com.personneltrackingsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "turnstile", schema = "dbpersonel")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Turnstile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "turnstileSeq")
    @SequenceGenerator(name = "turnstileSeq", allocationSize = 1)
    @Column(name = "turnstile_id")
    private Long turnstileId;

    private String turnstileName;


    @OneToMany
    @JoinColumn(name = "fk_gate_id")
    private Long gateId;
}

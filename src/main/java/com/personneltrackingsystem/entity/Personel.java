package com.personneltrackingsystem.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Entity
@Table(name = "personel", schema = "dbpersonel")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Personel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personel_id")
    private Long personelId;

    private String name;

    private String email;

    private Boolean administrator;

    private Double salary;


    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fk_unit_id")
    private Unit unit;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fk_gate_id")
    private Gate gate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_work_id")
    @Nullable
    private Work work;
}

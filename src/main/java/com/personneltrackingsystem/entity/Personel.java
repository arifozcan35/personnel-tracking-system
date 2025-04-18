package com.personneltrackingsystem.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "personel", schema = "dbpersonel")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Personel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "personel_id")
    private Long personelId;

    private String name;

    @NotBlank
    private String email;

    private Boolean administrator;

    private Double salary;


    @ManyToOne
    @JoinColumn(name = "fk_unit_id")
    private Unit unit;

    @ManyToOne
    @JoinColumn(name = "fk_gate_id")
    private Gate gate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_work_id")
    @Nullable
    private Work work;
}

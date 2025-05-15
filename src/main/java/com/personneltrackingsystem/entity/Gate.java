package com.personneltrackingsystem.entity;

import com.personneltrackingsystem.dto.DtoUnitIU;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "gate", schema = "dbpersonel")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Gate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gateSeq")
    @SequenceGenerator(name = "gateSeq", allocationSize = 1)
    @Column(name = "gate_id")
    private Long gateId;

    private String gateName;

    private Boolean mainEntrance;

    @ManyToOne
    @JoinColumn(name = "fk_unit_id")
    private DtoUnitIU unitId;
}

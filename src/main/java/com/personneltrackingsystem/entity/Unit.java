package com.personneltrackingsystem.entity;

import com.personneltrackingsystem.dto.DtoFloorIU;
import com.personneltrackingsystem.dto.DtoPersonelIU;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "unit", schema = "dbpersonel")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unitSeq")
    @SequenceGenerator(name = "unitSeq", allocationSize = 1)
    @Column(name = "unit_id")
    private Long unitId;

    private String unitName;

    @ManyToOne
    @JoinColumn(name = "fk_floor_id")
    private DtoFloorIU floorId;

    @OneToOne
    @JoinColumn(name = "fk_administrator_personel_id")
    private DtoPersonelIU administratorPersonelId;
}

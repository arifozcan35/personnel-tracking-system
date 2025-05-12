package com.personneltrackingsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "building", schema = "dbpersonel")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "buildingSeq")
    @SequenceGenerator(name = "buildingSeq", allocationSize = 1)
    @Column(name = "building_id")
    private Long buildingId;

    private String buildingName;

}

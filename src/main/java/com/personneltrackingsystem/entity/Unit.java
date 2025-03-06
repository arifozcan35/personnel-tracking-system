package com.personneltrackingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "unit", schema = "dbpersonel")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Long unitId;

    private String unitName;

    @OneToMany(mappedBy = "unit")
    @JsonIgnore
    private List<Personel> personels;


}

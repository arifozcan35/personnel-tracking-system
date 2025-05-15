package com.personneltrackingsystem.entity;

import java.util.List;

import com.personneltrackingsystem.dto.DtoPersonelTypeIU;
import com.personneltrackingsystem.dto.DtoUnitIU;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "personel", schema = "dbpersonel")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Personel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personelSeq")
    @SequenceGenerator(name = "personelSeq", allocationSize = 1)
    @Column(name = "personel_id")
    private Long personelId;

    private String name;

    @NotBlank
    private String email;


    @ManyToMany
    @JoinTable(name = "personel_unit",
    joinColumns = @JoinColumn(name = "personel_id"),
    inverseJoinColumns = @JoinColumn(name = "unit_id"))
    private List<DtoUnitIU> unitId;

    @ManyToOne
    @JoinColumn(name = "fk_personel_type_id")
    private DtoPersonelTypeIU personelTypeId;

}

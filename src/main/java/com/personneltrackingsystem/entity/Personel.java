package com.personneltrackingsystem.entity;

import java.util.List;

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
    @SequenceGenerator(name = "personelSeq", sequenceName = "personelSeq", allocationSize = 1)
    @Column(name = "personel_id")
    private Long personelId;

    private String name;

    @NotBlank
    private String email;


    @ManyToMany
    @JoinTable(name = "personel_unit",
    schema = "dbpersonel",
    joinColumns = @JoinColumn(name = "personel_id"),
    inverseJoinColumns = @JoinColumn(name = "unit_id"))
    private List<Unit> unitId;

    @ManyToOne
    @JoinColumn(name = "fk_personel_type_id")
    private PersonelType personelTypeId;

}

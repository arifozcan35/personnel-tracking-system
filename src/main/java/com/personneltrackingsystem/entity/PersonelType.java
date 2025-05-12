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
@Table(name = "personel_type", schema = "dbpersonel")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PersonelType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personelTypeSeq")
    @SequenceGenerator(name = "personelTypeSeq", allocationSize = 1)
    @Column(name = "personel_type_id")
    private Long personelTypeId;

    private String personelTypeName;  

}

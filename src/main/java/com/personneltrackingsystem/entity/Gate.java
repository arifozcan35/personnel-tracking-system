package com.personneltrackingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "gate", schema = "dbpersonel")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Gate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGen2")
    @SequenceGenerator(name = "mySeqGen2", allocationSize = 1)
    @Column(name = "gate_id")
    private Long gateId;

    private String gateName;


    @OneToMany(mappedBy = "gate")
    @JsonIgnore
    private List<Personel> personels;
}

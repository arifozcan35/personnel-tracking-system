package com.personneltrackingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gate_id")
    private Long gateId;

    @NotBlank
    private String gateName;


    @OneToMany(mappedBy = "gate")
    @JsonIgnore
    private List<Personel> personels;
}

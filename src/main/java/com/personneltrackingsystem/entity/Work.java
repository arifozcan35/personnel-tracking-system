package com.personneltrackingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "work", schema = "dbpersonel")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "work_id")
    private Long workId;

    // private LocalDate date;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    private Boolean isWorkValid;

    /*
    @OneToOne(mappedBy = "work")
    @JsonIgnore
    private Personel personel;
    */

}

package com.personneltrackingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

import com.personneltrackingsystem.dto.DtoPersonelTypeIU;

@Entity
@Table(name = "working_hours", schema = "dbpersonel")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class WorkingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workingHoursSeq")
    @SequenceGenerator(name = "workingHoursSeq", allocationSize = 1)
    @Column(name = "working_hours_id")
    private Long workingHoursId;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    @OneToOne
    @JoinColumn(name = "fk_personel_type_id")
    private DtoPersonelTypeIU personelTypeId;

}

package com.personneltrackingsystem.entity;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import com.personneltrackingsystem.dto.converter.YearMonthStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "salary", schema = "dbpersonel")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salarySeq")
    @SequenceGenerator(name = "salarySeq", sequenceName = "salarySeq", allocationSize = 1)
    @Column(name = "salary_id")
    private Long salaryId;

    @ManyToOne
    @JoinColumn(name = "fk_personel_id")
    private Personel personelId;
    
    @Column(name = "salary_month")
    @Convert(converter = YearMonthStringConverter.class)
    private YearMonth salaryMonth;
    
    @Column(name = "base_amount")
    private Double baseAmount;
    
    @Column(name = "late_days")
    private Integer lateDays;
    
    @Column(name = "late_penalty_amount")
    private Double latePenaltyAmount;
    
    @Column(name = "final_amount")
    private Double finalAmount;
    
    @Column(name = "calculation_date")
    private LocalDate calculationDate;
    
    @Column(name = "is_paid")
    private Boolean isPaid;
} 
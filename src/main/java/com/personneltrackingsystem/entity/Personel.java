package com.personneltrackingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Entity
@Table(name = "personel", schema = "dbpersonel")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Personel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personel_id")
    private Long personelId;

    private String name;

    private String email;

    private Boolean administrator;

    private Double salary;

    public Personel(Boolean administrator){
        if(administrator){
            this.salary = 40000.0;
        }
        else{
            this.salary = 30000.0;
        }
    }

    public Personel(Double salary){
        if(salary.equals(30000.0) || salary.equals(40000.0)){
            if(salary.equals(40000.0)){
                this.administrator = true;
                this.salary = 40000.0;
            }else{
                this.administrator = false;
                this.salary = 30000.0;
            }
        }else{
            if (Math.abs(salary - 30000.0) < Math.abs(salary - 40000.0)) {
                this.administrator = false;
                this.salary = 30000.0;
            } else {
                this.administrator = true;
                this.salary = 40000.0;
            }
            new ResponseEntity<>("The salary can be only 30000.0 or 40000.0! The value you entered is assigned " +
                    "to the value that is closer to these two values (" + this.salary + ")!" , HttpStatus.BAD_REQUEST);
        }
    }

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fk_unit_id")
    private Unit unit;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fk_gate_id")
    private Gate gate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_work_id")
    private Work work;
}

package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Work;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoPersonelIU {

    private String name;

    private String email;

    private Boolean administrator;

    private Double salary;


    public DtoPersonelIU(Boolean administrator){
        if(administrator == true){
            this.salary = 40000.0;
        }
        else{
            this.salary = 30000.0;
        }
    }

    public DtoPersonelIU(Double salary){
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


    private DtoUnit unit;

    private DtoGate gate;

    private Work work;
}

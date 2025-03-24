package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.PersonelController;
import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Work;
import com.personneltrackingsystem.service.PersonelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Personel Controller", description = "Personnel CRUD operations, working hours validity check and salary transactions")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personel")
public class PersonelControllerImpl implements PersonelController {

    private final PersonelService personelServiceImpl;


    @GetMapping
    @Override
    public List<DtoPersonel> getAllPersonels() {
        return personelServiceImpl.getAllPersonels();
    }


    /*
    @GetMapping("/{personelId}")
    public Personel getAOnePersonel(@PathVariable Long personelId) {
        return personelService.getAOnePersonel(personelId);
    }
    */


    // the same usage above function
    @GetMapping("/{personelId}")
    @Override
    public DtoPersonel getOnePersonel(@PathVariable Long personelId){
        return personelServiceImpl.getAOnePersonel(personelId);
    }


    @PostMapping
    @Override
    public ResponseEntity<String> createPersonel(@RequestBody Personel newPersonel) {
        return personelServiceImpl.saveOnePersonel(newPersonel);
    }


    @PutMapping("/{personelId}")
    @Override
    public ResponseEntity<String> updatePersonel(@PathVariable Long personelId, @RequestBody Personel newPersonel) {
        return personelServiceImpl.updateOnePersonel(personelId, newPersonel);
    }


    @DeleteMapping("/{personelId}")
    @Override
    public void deletePersonel(@PathVariable Long personelId) {
        personelServiceImpl.deleteOnePersonel(personelId);
    }



    @Operation(
            summary = "Working hour calculation",
            description = "Calculates whether the working hours of the personnel whose ID is given are valid."
    )
    @PostMapping("/salary/{personelId}")
    @Override
    public DtoPersonel seeSalary(@PathVariable Long personelId) {
        return personelServiceImpl.calculateSalaryByPersonelId(personelId);
    }


    @Operation(
            summary = "Viewing working hours information",
            description = "The working hours information of the personnel belonging to the given id is displayed."
    )
    @GetMapping("/work/{personelId}")
    @Override
    public Work seeWork(@PathVariable Long personelId) {
        return personelServiceImpl.getOneWorkofPersonel(personelId);
    }


    @Operation(
            summary = "Salary information",
            description = "Salary information of all personnel in the system is displayed."
    )
    @GetMapping("/salaries")
    @Override
    public Map<String, Double> getAllSalaries() {
        Map<String, Double> salaries = personelServiceImpl.listSalaries();
        return salaries;
    }

}

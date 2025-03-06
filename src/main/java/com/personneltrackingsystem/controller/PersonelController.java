package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.service.WorkService;
import com.personneltrackingsystem.service.PersonelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personel")
public class PersonelController {
    @Autowired
    private PersonelService personelService;

    @Autowired
    private WorkService workService;

    @GetMapping
    public List<Personel> getAllPersonels() {
        return personelService.getAllPersonels();
    }

    /*
    @GetMapping("/{personelId}")
    public Personel getAOnePersonel(@PathVariable Long personelId) {
        return personelService.getAOnePersonel(personelId);
    }
    */

    // the same usage above function
    @GetMapping("/{personelId}")
    public Personel getOnePersonel(@PathVariable(name="id", required = true) Long personelId){
        return personelService.getAOnePersonel(personelId);
    }



    @PostMapping
    public ResponseEntity<String> createPersonel(@RequestBody Personel newPersonel) {
        return personelService.saveOnePersonel(newPersonel);
    }

    @PutMapping("/{personelId}")
    public ResponseEntity<String> updatePersonel(@PathVariable Long personelId, @RequestBody Personel newPersonel) {
        return personelService.updateOnePersonel(personelId, newPersonel);
    }

    @DeleteMapping("/{personelId}")
    public void deletePersonel(@PathVariable Long personelId) {
        personelService.deleteOnePersonel(personelId);
    }

    @GetMapping("/salary/{personelId}")
    public void seeSalary(@PathVariable Long personelId) {
        personelService.workHoursCalculate(personelId);
    }

}

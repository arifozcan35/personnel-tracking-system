package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.IPersonelController;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.service.IPersonelService;
import com.personneltrackingsystem.service.Impl.WorkServiceImpl;
import com.personneltrackingsystem.service.Impl.PersonelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personel")
public class PersonelControllerImpl implements IPersonelController {

    @Autowired
    private IPersonelService personelServiceImpl;

    @Autowired
    private WorkServiceImpl workServiceImpl;

    @GetMapping
    @Override
    public List<Personel> getAllPersonels() {
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
    public Personel getOnePersonel(@PathVariable Long personelId){
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

    @GetMapping("/salary/{personelId}")
    @Override
    public void seeSalary(@PathVariable Long personelId) {
        personelServiceImpl.workHoursCalculate(personelId);
    }

}

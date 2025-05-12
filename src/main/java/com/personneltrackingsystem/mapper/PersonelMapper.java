package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.WorkingHours;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PersonelMapper {

    List<DtoPersonel> personelsToDtoPersonels(List<Personel> personelList);


    DtoPersonel personelToDtoPersonel(Personel personel);


    DtoPersonelIU personelToDtoPersonelIU(Personel personel);

    Personel dtoPersonelIUToPersonel(DtoPersonelIU personelIU);


    // converts personnel list to Map<String, Double>
    default Map<String, Double> personelsToSalaryMap(List<Personel> personels) {
        if (personels == null) {
            return Collections.emptyMap();
        }

        return personels.stream()
                .collect(Collectors.toMap(
                        Personel::getEmail,
                        Personel::getSalary
                ));
    }


    WorkingHours DbWorktoWork(Optional<WorkingHours> dbWorkOpt);
}

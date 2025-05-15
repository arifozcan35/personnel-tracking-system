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


    
    WorkingHours DbWorktoWork(Optional<WorkingHours> dbWorkOpt);
}

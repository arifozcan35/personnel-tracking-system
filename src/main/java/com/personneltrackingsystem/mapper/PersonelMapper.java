package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.entity.Personel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface PersonelMapper {

    PersonelMapper INSTANCE = Mappers.getMapper(PersonelMapper.class);

    List<DtoPersonel> personelsToDtoPersonels(List<Personel> personelList);


    DtoPersonel personelToDtoPersonel(Personel personel);


    DtoPersonelIU personelToDtoPersonelIU(Personel personel);


}

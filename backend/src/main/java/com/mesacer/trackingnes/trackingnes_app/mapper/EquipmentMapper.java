package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.mesacer.trackingnes.trackingnes_app.dto.EquipmentDTO;
import com.mesacer.trackingnes.trackingnes_app.model.Equipment;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
    EquipmentMapper INSTANCE = Mappers.getMapper(EquipmentMapper.class);

    @Mapping(source = "section.id", target = "sectionId")
    @Mapping(source = "section.description", target = "sectionDescription")
    EquipmentDTO toDTO(Equipment equipment);

    @Mapping(source = "sectionId", target = "section.id")
    Equipment toEntity(EquipmentDTO equipmentDTO);

}

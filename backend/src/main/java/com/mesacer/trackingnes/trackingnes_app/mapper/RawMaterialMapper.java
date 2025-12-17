package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.mesacer.trackingnes.trackingnes_app.dto.RawMaterialDTO;
import com.mesacer.trackingnes.trackingnes_app.model.RawMaterial;

@Mapper(componentModel = "spring", uses = { PhaseMapper.class })
public interface RawMaterialMapper {

    RawMaterialMapper INSTANCE = Mappers.getMapper(RawMaterialMapper.class);

    @Mapping(source = "section.description", target = "sectionDescription")
    RawMaterialDTO toDTO(RawMaterial entity);

    RawMaterial toEntity(RawMaterialDTO dto);
}

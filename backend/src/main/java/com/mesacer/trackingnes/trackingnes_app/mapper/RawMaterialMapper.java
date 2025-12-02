package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.mesacer.trackingnes.trackingnes_app.dto.RawMaterialDTO;
import com.mesacer.trackingnes.trackingnes_app.model.RawMaterial;

@Mapper(componentModel = "spring")
public interface RawMaterialMapper {

    RawMaterialMapper INSTANCE = Mappers.getMapper(RawMaterialMapper.class);

    RawMaterialDTO toDTO(RawMaterial entity);

    RawMaterial toEntity(RawMaterialDTO dto);
}

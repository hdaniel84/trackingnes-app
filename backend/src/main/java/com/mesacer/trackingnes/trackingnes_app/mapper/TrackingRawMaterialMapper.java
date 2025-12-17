package com.mesacer.trackingnes.trackingnes_app.mapper;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRawMaterialRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRawMaterialResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingRawMaterial; // Tu Entidad
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrackingRawMaterialMapper {

    // INPUT
    @Mapping(target = "rawMaterialType.id", source = "rawMaterialId")
    @Mapping(target = "tracking", ignore = true) // Se asigna en el padre o servicio
    TrackingRawMaterial toEntity(TrackingRawMaterialRequestDTO dto);

    // OUTPUT
    @Mapping(target = "rawMaterialId", source = "rawMaterialType.id")
    @Mapping(target = "rawaMaterialDescription", source = "rawMaterialType.description") 
    TrackingRawMaterialResponseDTO toResponseDTO(TrackingRawMaterial entity);
}

package com.mesacer.trackingnes.trackingnes_app.mapper;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingParameterRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingParameterResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingParameter; // Tu Entidad
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrackingParameterMapper {

    // INPUT
    @Mapping(target = "parameter.id", source = "parameterId")
    @Mapping(target = "tracking", ignore = true) // Se asigna en el padre o servicio
    TrackingParameter toEntity(TrackingParameterRequestDTO dto);

    // OUTPUT
    // Sacamos la descripción de la relación con la entidad Parameter
    @Mapping(target = "parameterId", source = "parameter.id")
    @Mapping(target = "parameterDescription", source = "parameter.description") 
    TrackingParameterResponseDTO toResponseDTO(TrackingParameter entity);
}

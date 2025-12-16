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

/* 

package com.mesacer.trackingnes.trackingnes_app.mapper;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingParameterDTO;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrackingParameterMapper {

    @Mapping(source = "tracking.id", target = "trackingId")
    @Mapping(source = "parameter.id", target = "parameterId")
    @Mapping(source = "parameter.description", target = "parameterTypeDescription")
    TrackingParameterDTO toDto(TrackingParameter trackingParameter);

    // Para toEntity, dejamos que Tracking y Parameter se asignen manualmente en el
    // Service
    @Mapping(target = "tracking", ignore = true)
    @Mapping(source = "parameterId", target = "parameter.id")
    TrackingParameter toEntity(TrackingParameterDTO dto);

}
    */

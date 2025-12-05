package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mesacer.trackingnes.trackingnes_app.dto.ParameterVidragemDTO;
import com.mesacer.trackingnes.trackingnes_app.model.ParameterVidragem;

@Mapper(componentModel = "spring")
public interface ParameterVidragemMapper {

    @Mapping(source = "id", target = "trackingId")
    @Mapping(source = "parameter.id", target = "parameterId")
    @Mapping(source = "parameter.description", target = "parameterDescription")
    ParameterVidragemDTO toDTO(ParameterVidragem entity);

    @Mapping(source = "trackingId", target = "id")
    @Mapping(source = "parameterId", target = "parameter.id")
    @Mapping(source = "parameterDescription", target = "parameter.description")
    ParameterVidragem toEntity(ParameterVidragemDTO dto);
}

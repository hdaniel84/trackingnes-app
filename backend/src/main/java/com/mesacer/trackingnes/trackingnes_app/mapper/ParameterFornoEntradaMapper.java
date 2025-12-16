package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mesacer.trackingnes.trackingnes_app.dto.ParameterFornoEntradaDTO;
import com.mesacer.trackingnes.trackingnes_app.model.ParameterFornoEntrada;

@Mapper(componentModel = "spring")
public interface ParameterFornoEntradaMapper {

    @Mapping(source = "id", target = "trackingId")
    @Mapping(source = "parameter.id", target = "parameterId")
    @Mapping(source = "parameter.description", target = "parameterDescription")
    ParameterFornoEntradaDTO toDTO(ParameterFornoEntrada entity);

    @Mapping(source = "trackingId", target = "id")
    @Mapping(source = "parameterId", target = "parameter.id")
    @Mapping(source = "parameterDescription", target = "parameter.description")
    ParameterFornoEntrada toEntity(ParameterFornoEntradaDTO dto);
}

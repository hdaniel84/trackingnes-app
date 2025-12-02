package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mesacer.trackingnes.trackingnes_app.dto.ParametersPrensasDTO;
import com.mesacer.trackingnes.trackingnes_app.model.ParametersPrensas;

@Mapper(componentModel = "spring")
public interface ParametersPrensasMapper {

    @Mapping(source = "id", target = "trackingId")
    @Mapping(source = "parameter.id", target = "parameterId")
    ParametersPrensasDTO toDTO(ParametersPrensas entity);

    @Mapping(source = "trackingId", target = "id")
    @Mapping(source = "parameterId", target = "parameter.id")
    ParametersPrensas toEntity(ParametersPrensasDTO dto);
}

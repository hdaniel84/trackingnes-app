package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import com.mesacer.trackingnes.trackingnes_app.dto.ParameterDTO;
import com.mesacer.trackingnes.trackingnes_app.model.Parameter;

@Mapper(componentModel = "spring", uses = { PhaseMapper.class })
public interface ParameterMapper {
    ParameterDTO toDTO(Parameter parameter);
    Parameter toEntity(ParameterDTO dto);
}

package com.mesacer.trackingnes.trackingnes_app.mapper;

import com.mesacer.trackingnes.trackingnes_app.dto.PhaseDTO;
import com.mesacer.trackingnes.trackingnes_app.model.Phase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PhaseMapper {

    PhaseDTO toDto(Phase phase);

    Phase toEntity(PhaseDTO dto);
}

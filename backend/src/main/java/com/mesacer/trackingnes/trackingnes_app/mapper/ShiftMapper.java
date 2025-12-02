package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import com.mesacer.trackingnes.trackingnes_app.dto.ShiftDTO;
import com.mesacer.trackingnes.trackingnes_app.model.Shift;

@Mapper(componentModel = "spring")
public interface ShiftMapper {
    ShiftDTO toDTO(Shift shift);
    Shift toEntity(ShiftDTO dto);
}

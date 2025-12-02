package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import com.mesacer.trackingnes.trackingnes_app.dto.SectionDTO;
import com.mesacer.trackingnes.trackingnes_app.model.Section;

@Mapper(componentModel = "spring")
public interface SectionMapper {
    SectionDTO toDTO(Section section);
    Section toEntity(SectionDTO dto);
}

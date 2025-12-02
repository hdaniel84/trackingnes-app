package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mesacer.trackingnes.trackingnes_app.dto.TeamDTO;
import com.mesacer.trackingnes.trackingnes_app.model.Team;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(source = "section.id", target = "sectionId")
    @Mapping(source = "section.description", target = "sectionDescription")
    @Mapping(source = "responsable.id", target = "responsableId")
    TeamDTO toDTO(Team team);

    @Mapping(source = "sectionId", target = "section.id")
    @Mapping(source = "responsableId", target = "responsable.id")
    Team toEntity(TeamDTO dto);
}

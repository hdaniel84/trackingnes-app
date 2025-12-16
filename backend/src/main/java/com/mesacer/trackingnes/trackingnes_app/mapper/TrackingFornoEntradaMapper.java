package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingFornoEntradaDTO;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingFornoEntrada;

@Mapper(componentModel = "spring", uses = { ParameterFornoEntradaMapper.class })
public interface TrackingFornoEntradaMapper {

    @Mapping(source = "team.id", target = "teamId")
    @Mapping(source = "team.description", target = "teamDescription")
    @Mapping(source = "team.section.description", target = "teamSectionDescription")

    @Mapping(source = "shift.id", target = "shiftId")
    @Mapping(source = "shift.description", target = "shiftDescription")

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.description", target = "productDescription")

    TrackingFornoEntradaDTO toDTO(TrackingFornoEntrada entity);

    @Mapping(source = "teamId", target = "team.id")
    @Mapping(source = "shiftId", target = "shift.id")
    @Mapping(source = "productId", target = "product.id")
    TrackingFornoEntrada toEntity(TrackingFornoEntradaDTO dto);

}

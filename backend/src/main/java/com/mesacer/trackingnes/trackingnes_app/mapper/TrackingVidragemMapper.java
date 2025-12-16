package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingVidragemDTO;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingVidragem;

@Mapper(componentModel = "spring", uses = { ParameterVidragemMapper.class })
public interface TrackingVidragemMapper {

    @Mapping(source = "team.id", target = "teamId")
    @Mapping(source = "team.description", target = "teamDescription")
    @Mapping(source = "team.section.description", target = "teamSectionDescription")

    @Mapping(source = "shift.id", target = "shiftId")
    @Mapping(source = "shift.description", target = "shiftDescription")

    @Mapping(source = "rawMaterial.id", target = "rawMaterialId")
    @Mapping(source = "rawMaterial.sapCode", target = "rawMaterialSapCode")
    @Mapping(source = "rawMaterial.description", target = "rawMaterialDescription")
    @Mapping(source = "rawMaterial.section.description", target = "rawMaterialSection")

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.description", target = "productDescription")

    @Mapping(source = "equipment.id", target = "equipmentId")
    @Mapping(source = "equipment.description", target = "equipmentDescription")
    TrackingVidragemDTO toDTO(TrackingVidragem entity);

    @Mapping(source = "teamId", target = "team.id")
    @Mapping(source = "shiftId", target = "shift.id")
    @Mapping(source = "rawMaterialId", target = "rawMaterial.id")
    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "equipmentId", target = "equipment.id")
    TrackingVidragem toEntity(TrackingVidragemDTO dto);

}

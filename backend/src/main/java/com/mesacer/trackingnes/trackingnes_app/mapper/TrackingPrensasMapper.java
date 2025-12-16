package com.mesacer.trackingnes.trackingnes_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingPrensasDTO;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingPrensas;

@Mapper(componentModel = "spring", uses = { ParametersPrensasMapper.class })
public interface TrackingPrensasMapper {

    @Mapping(source = "team.id", target = "teamId")
    @Mapping(source = "team.description", target = "teamDescription")
    @Mapping(source = "team.section.description", target = "teamSectionDescription")

    @Mapping(source = "shift.id", target = "shiftId")
    @Mapping(source = "shift.description", target = "shiftDescription")

    @Mapping(source = "rawMaterial.id", target = "rawMaterialId")
    @Mapping(source = "rawMaterial.sapCode", target = "rawMaterialSapCode")
    @Mapping(source = "rawMaterial.description", target = "rawMaterialDescription")

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.codigoProduto", target = "productCode")
    @Mapping(source = "product.description", target = "productDescription")
    @Mapping(source = "product.shape.id", target = "productShapeId")
    

    @Mapping(source = "equipment.id", target = "equipmentId")
    @Mapping(source = "equipment.description", target = "equipmentDescription")
    TrackingPrensasDTO toDTO(TrackingPrensas entity);

    @Mapping(source = "teamId", target = "team.id")
    @Mapping(source = "shiftId", target = "shift.id")
    @Mapping(source = "rawMaterialId", target = "rawMaterial.id")
    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "equipmentId", target = "equipment.id")
    TrackingPrensas toEntity(TrackingPrensasDTO dto);

}

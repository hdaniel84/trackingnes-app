package com.mesacer.trackingnes.trackingnes_app.mapper;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.model.*;

import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { TrackingParameterMapper.class, TrackingRawMaterialMapper.class,
        TeamMapper.class, EntityReferenceMapper.class })
public interface TrackingMapper {

    // --- MAPPING DE ENTRADA (Request -> Entity) ---
    // Mapeamos directamente a la entidad (usando los helpers de abajo)
    @Mapping(target = "team", source = "teamId")
    @Mapping(target = "shift", source = "shiftId")
    @Mapping(target = "product", source = "productId")
    @Mapping(target = "equipment", source = "equipmentId")
    @Mapping(target = "phase", source = "phaseId")
    @Mapping(target = "trackingSource", source = "trackingSourceId")
    @Mapping(target = "auxiliaryEquipments", source = "auxiliaryEquipmentIds")

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Tracking toEntity(TrackingRequestDTO dto);

    // --- MAPPING DE ACTUALIZACIÓN ---

    @InheritConfiguration
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TrackingRequestDTO dto, @MappingTarget Tracking entity);

    // --- MAPPING DE SALIDA ---
    // Extraemos el ID del objeto padre
    @Mapping(target = "trackingSourceId", source = "trackingSource.id")
    @Mapping(target = "trackingSourceProductDescription", source = "trackingSource.product.description")
    @Mapping(target = "createdByUsername", ignore = true)
    TrackingResponseDTO toResponseDTO(Tracking entity);

    default Tracking mapTrackingRef(Long id) {
        if (id == null)
            return null;
        Tracking t = new Tracking();
        t.setId(id);
        return t;
    }

}
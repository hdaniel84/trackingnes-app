package com.mesacer.trackingnes.trackingnes_app.mapper;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.model.Tracking; // Tu entidad
import com.mesacer.trackingnes.trackingnes_app.model.TrackingComposition;

import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
        TrackingParameterMapper.class,
        TrackingRawMaterialMapper.class,
        TeamMapper.class,
        EntityReferenceMapper.class,
        ProductMapper.class
})
public interface TrackingMapper {

    // --- MAPPING DE ENTRADA (Request -> Entity) ---
    @Mapping(target = "team", source = "teamId")
    @Mapping(target = "shift", source = "shiftId")
    @Mapping(target = "product", source = "productId")
    @Mapping(target = "phase", source = "phaseId")
    @Mapping(target = "auxiliaryEquipments", source = "auxiliaryEquipmentIds")

    // Ignorados
    @Mapping(target = "sourceComposition", ignore = true)
    @Mapping(target = "destinationComposition", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Tracking toEntity(TrackingRequestDTO dto);

    // --- MAPPING DE ACTUALIZACIÓN ---
    @InheritConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rawMaterials", ignore = true)
    @Mapping(target = "parameters", ignore = true)      
    @Mapping(target = "sourceComposition", ignore = true)
    void updateEntityFromDto(TrackingRequestDTO dto, @MappingTarget Tracking entity);

    // --- MAPPING DE SALIDA ---

    @Mapping(target = "createdByUsername", ignore = true)
    @Mapping(target = "sources", source = "sourceComposition")
    @Mapping(target = "remainingQuantity", source = "availability.remainingQuantity")
    TrackingResponseDTO toResponseDTO(Tracking entity);

    // --- HELPER NUEVO: TrackingComposition -> SourceSummaryDTO ---
    // Extrae solo los datos planos.
    @Mapping(target = "trackingId", source = "parent.id")
    @Mapping(target = "quantityUsed", source = "quantityUsed")
    @Mapping(target = "productCode", source = "parent.product.productCode")
    @Mapping(target = "productDescription", source = "parent.product.description")
    @Mapping(target = "remainingQuantity", source = "parent.availability.remainingQuantity")
    TrackingResponseDTO.SourceSummaryDTO toSourceSummary(TrackingComposition composition);

    // --- HELPER EXISTENTE: Long -> Entity (Referencia) ---
    // Se usa para convertir los IDs de entrada en objetos dummy para JPA
    default Tracking mapTrackingRef(Long id) {
        if (id == null)
            return null;
        Tracking t = new Tracking();
        t.setId(id);
        return t;
    }
}
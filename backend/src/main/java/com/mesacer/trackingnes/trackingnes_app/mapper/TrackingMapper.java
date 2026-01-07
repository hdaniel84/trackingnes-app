package com.mesacer.trackingnes.trackingnes_app.mapper;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.model.Tracking; // Tu entidad
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { 
    TrackingParameterMapper.class, 
    TrackingRawMaterialMapper.class,
    TeamMapper.class, 
    EntityReferenceMapper.class 
})
public interface TrackingMapper {

    // --- MAPPING DE ENTRADA (Request -> Entity) ---
    @Mapping(target = "team", source = "teamId")
    @Mapping(target = "shift", source = "shiftId")
    @Mapping(target = "product", source = "productId")
    @Mapping(target = "equipment", source = "equipmentId")
    @Mapping(target = "phase", source = "phaseId")
    @Mapping(target = "auxiliaryEquipments", source = "auxiliaryEquipmentIds")
    @Mapping(target = "sourceTrackings", source = "sourceTrackingIds")

    // Ignorados
    @Mapping(target = "destinationTrackings", ignore = true) // Normalmente no seteamos los hijos al crear el padre
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
    
    // CAMBIO IMPORTANTE: Mapear la lista de entidades a lista de DTOs resumen
    @Mapping(target = "sourceTrackings", source = "sourceTrackings") 
    @Mapping(target = "createdByUsername", ignore = true) 
    TrackingResponseDTO toResponseDTO(Tracking entity);


    // --- HELPER NUEVO: Entity -> Summary DTO ---
    // Este método le enseña a MapStruct cómo convertir UN solo tracking en su resumen.
    // MapStruct lo usará para llenar la lista List<TrackingSourceSummaryDTO>
    @Mapping(target = "productDescription", source = "product.description")
    @Mapping(target = "logisticUnits", source = "logisticUnits")
    TrackingResponseDTO.TrackingSourceSummaryDTO toSummaryDTO(Tracking entity);


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
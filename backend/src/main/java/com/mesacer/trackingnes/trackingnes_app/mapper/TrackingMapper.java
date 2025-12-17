package com.mesacer.trackingnes.trackingnes_app.mapper;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.model.*; // Importar todas las entidades
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { TrackingParameterMapper.class, TrackingRawMaterialMapper.class })
public interface TrackingMapper {

    // --- MAPPING DE ENTRADA (Request -> Entity) ---
    // Mapeamos directamente a la entidad (usando los helpers de abajo)
    @Mapping(target = "team", source = "teamId")
    @Mapping(target = "shift", source = "shiftId")
    @Mapping(target = "product", source = "productId")
    @Mapping(target = "equipment", source = "equipmentId")
    @Mapping(target = "phase", source = "phaseId")
    @Mapping(target = "trackingSource", source = "trackingSourceId")

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

    // ========================================================================
    // HELPERS: Convierten Long -> Entidad (Referencia)
    // Esto evita que MapStruct intente modificar el ID de la entidad existente
    // ========================================================================

    default Team mapTeam(Long id) {
        if (id == null)
            return null;
        Team t = new Team();
        t.setId(id);
        return t;
    }

    default Shift mapShift(Long id) {
        if (id == null)
            return null;
        Shift s = new Shift();
        s.setId(id);
        return s;
    }

    default Product mapProduct(Long id) {
        if (id == null)
            return null;
        Product p = new Product();
        p.setId(id);
        return p;
    }

    default Equipment mapEquipment(Long id) {
        if (id == null)
            return null;
        Equipment e = new Equipment();
        e.setId(id);
        return e;
    }

    default Phase mapPhase(Long id) {
        if (id == null)
            return null;
        Phase p = new Phase();
        p.setId(id);
        return p;
    }

    default Tracking mapTrackingRef(Long id) {
        if (id == null)
            return null;
        Tracking t = new Tracking();
        t.setId(id);
        return t;
    }

}
package com.mesacer.trackingnes.trackingnes_app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TrackingResponseDTO {

    private Long id;
    
    // Aquí sí devolvemos el objeto completo para que el Front pueda mostrar descripciones
    private TeamDTO team;
    private ShiftDTO shift;
    private ProductDTO product;
    private EquipmentDTO equipment;
    private PhaseDTO phase;

    private Long logisticUnit;
    private Integer quantity;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Long trackingSourceId;
    // Opcional: Info extra para mostrar en el frontend sin hacer otra petición
    // Ej: "Producto X (Fase Anterior)"
    private String trackingSourceProductDescription;
    
    // Audit fields (Solo lectura)
    private LocalDateTime createdDate;
    private String createdByUsername; // Ojo: A veces es mejor devolver el nombre que el ID
    private LocalDateTime updatedDate;

    private String comments;

    private List<TrackingParameterResponseDTO> parameters;

    private List<TrackingRawMaterialResponseDTO> rawMaterials;
}
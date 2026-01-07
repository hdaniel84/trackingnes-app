package com.mesacer.trackingnes.trackingnes_app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TrackingResponseDTO {

    private Long id;
    private TeamDTO team;
    private ShiftDTO shift;
    private ProductDTO product;
    private EquipmentDTO equipment;
    private PhaseDTO phase;
    private List<EquipmentDTO> auxiliaryEquipments;

    private List<Long> logisticUnits;
    private Integer quantity;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<TrackingSourceSummaryDTO> sourceTrackings;

    // Audit fields (Solo lectura)
    private LocalDateTime createdDate;
    private String createdByUsername; // Ojo: A veces es mejor devolver el nombre que el ID
    private LocalDateTime updatedDate;

    private String comments;

    private List<TrackingParameterResponseDTO> parameters;

    private List<TrackingRawMaterialResponseDTO> rawMaterials;

    @Data
    public static class TrackingSourceSummaryDTO {
        private Long id;
        private List<Long> logisticUnits;
        private String productDescription;
    }
}
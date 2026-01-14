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
    private PhaseDTO phase;
    private List<EquipmentDTO> auxiliaryEquipments;

    private List<Long> logisticUnits;
    private Integer quantity;
    private Integer quantityScrap;
    private Integer remainingQuantity;
    private Integer quantitySecond;
    private Integer quantityRework;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<SourceSummaryDTO> sources;

    // Audit fields (Solo lectura)
    private LocalDateTime createdDate;
    private String createdByUsername;
    private LocalDateTime updatedDate;

    private String comments;

    private List<TrackingParameterResponseDTO> parameters;

    private List<TrackingRawMaterialResponseDTO> rawMaterials;

    // Clase interna para resumir la composición sin exponer Entidades JPA
    @Data
    public static class SourceSummaryDTO {
        private Long trackingId;
        private String productCode;
        private String productDescription;
        private Double quantityUsed;
        private Integer remainingQuantity;
    }

}
package com.mesacer.trackingnes.trackingnes_app.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class TrackingVidragemDTO {
    private Long id;

    @NotNull
    private Long teamId;
    private String teamDescription;
    private String teamSectionDescription;

    @NotNull
    private Long shiftId;
    private String shiftDescription;

    @NotNull
    private Long productId;
    private String productDescription;

    @NotNull
    private Long equipmentId;
    private String equipmentDescription;

    @NotNull
    @Min(value = 0, message = "Campo não válido")
    private Integer logisticUnitInId;

    @NotNull
    @Min(value = 0, message = "Campo não válido")
    private Integer logisticUnitOutId;

    @NotNull
    @Min(value = 0, message = "Campo não válido")
    private Integer quantity;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @NotNull
    private Long rawMaterialId;
    private String rawMaterialSapCode;          
    private String rawMaterialDescription;
    private String rawMaterialSection;
    
    @NotNull
    @NotBlank
    private String lote;

    private List<ParameterVidragemDTO> parameters;

    private String comments;
    private LocalDateTime createdDate;
    private Long createdBy;
}

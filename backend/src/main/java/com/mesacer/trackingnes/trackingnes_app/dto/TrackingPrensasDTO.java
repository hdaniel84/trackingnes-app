package com.mesacer.trackingnes.trackingnes_app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class TrackingPrensasDTO {
    private Long id;
    private LocalDateTime startTime;

    @NotNull
    private Long teamId;
    private String teamDescription;
    private String teamSectionDescription;

    @NotNull
    private Long shiftId;
    private String shiftDescription;

    @NotNull
    private Long rawMaterialId;
    private String rawMaterialSapCode;          
    private String rawMaterialDescription;
    
    @NotNull
    @NotBlank
    private String lote;

    @NotNull
    private Long productId;
    private String productCode;
    private String productDescription;
    private String productShapeId;

    @NotNull
    @Min(value = 0, message = "Campo não válido")
    private Integer quantity;

    @NotNull
    @Min(value = 0, message = "Campo não válido")
    private Integer logisticUnit;
    private String comments;

    @NotNull
    private Long equipmentId;
    private String equipmentDescription;

    private LocalDateTime createdTime;
    private Long createdUserId;
    private LocalDateTime endTime;

    private List<ParametersPrensasDTO> parameters;
}

package com.mesacer.trackingnes.trackingnes_app.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrackingFornoEntradaDTO {
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
    @Min(value = 0, message = "Campo não válido")
    private Integer logisticUnitInId;

    @NotNull
    @Min(value = 0, message = "Campo não válido")
    private Integer wagonId;

    @NotNull
    @Min(value = 0, message = "Campo não válido")
    private Integer quantity;

    private LocalDateTime startTime;

    private List<ParameterFornoEntradaDTO> parameters;

    private String comments;
    private LocalDateTime createdDate;
    private Long createdBy;
}

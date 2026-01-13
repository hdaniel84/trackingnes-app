package com.mesacer.trackingnes.trackingnes_app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingDTO {

    private Long id;

    @Valid
    @NotNull(message = "El equipo es obligatorio")
    private TeamDTO team;

    @Valid
    @NotNull(message = "El turno es obligatorio")
    private ShiftDTO shift;

    @Valid
    @NotNull(message = "El producto es obligatorio")
    private ProductDTO product;

    @Valid
    @NotNull(message = "La fase es obligatoria")
    private PhaseDTO phase;

    @Positive(message = "La unidad logística debe ser positiva")
    private Long logisticUnit;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor que cero")
    private Integer quantity;

    @NotNull(message = "El startTime es obligatorio")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private RawMaterialDTO rawMaterial;

    private Long trackingSourceId;

    @Size(max = 5000, message = "Los comentarios no pueden superar 5000 caracteres")
    private String comments;

    @Valid
    private List<TrackingParameterDTO> parameters;

    private LocalDateTime createdDate;
    private Long createdBy;
    private LocalDateTime updatedDate;
    private Long updatedBy;
}

package com.mesacer.trackingnes.trackingnes_app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TrackingRequestDTO {

    // Opcional: Puede ir nulo en creación, obligatorio en actualización (o manejado por PathVariable)
    private Long id;

    @NotNull(message = "El ID del equipo es obligatorio")
    private Long teamId;

    @NotNull(message = "El ID del turno es obligatorio")
    private Long shiftId;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @NotNull(message = "El ID de la máquina es obligatorio")
    private Long equipmentId;

    private List<Long> auxiliaryEquipmentIds;

    // Asumo que la fase puede venir por defecto o ser seleccionada
    private Long phaseId;

    @Positive(message = "La unidad logística debe ser positiva")
    private Long logisticUnit;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor que cero")
    private Integer quantity;

    @NotNull(message = "El startTime es obligatorio")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long rawMaterialId;

    // Solo necesitamos el ID del registro origen
    private Long trackingSourceId;

    @Size(max = 5000, message = "Los comentarios no pueden superar 5000 caracteres")
    private String comments;

    @Valid
    private List<TrackingParameterRequestDTO> parameters;

    @Valid
    private List<TrackingRawMaterialRequestDTO> rawMaterials;
}
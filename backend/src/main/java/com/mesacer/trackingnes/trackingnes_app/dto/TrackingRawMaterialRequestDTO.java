package com.mesacer.trackingnes.trackingnes_app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TrackingRawMaterialRequestDTO {

    // Null si es nuevo, con valor si se está editando una línea específica
    private Long id;

    @NotNull(message = "El ID es obligatorio")
    private Long rawMaterialId;

    // Enviamos el valor como String (el backend lo parsea si es necesario)
    @Size(max = 100, message = "El valor no puede superar 100 caracteres")
    private String value;
    
}
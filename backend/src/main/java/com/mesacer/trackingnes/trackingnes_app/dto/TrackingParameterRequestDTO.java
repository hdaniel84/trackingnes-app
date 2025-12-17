package com.mesacer.trackingnes.trackingnes_app.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TrackingParameterRequestDTO {

    // Null si es nuevo, con valor si se está editando una línea específica
    private Long id;

    @NotNull(message = "El ID del parámetro es obligatorio")
    private Long parameterId;

    // Enviamos el valor como String (el backend lo parsea si es necesario)
    @Size(max = 5000, message = "El valor no puede superar 5000 caracteres")
    private String valueString;
    private Double valueNumber;

    private Boolean valueBool;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime valueDate;
}
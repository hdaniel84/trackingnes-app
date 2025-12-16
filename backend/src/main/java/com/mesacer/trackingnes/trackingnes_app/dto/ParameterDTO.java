package com.mesacer.trackingnes.trackingnes_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDTO {

    private Long id;

    @NotBlank(message = "La descripción del parámetro es obligatoria")
    @Size(max = 100, message = "La descripción no puede superar 100 caracteres")
    private String description;

    @NotNull(message = "El ID de fase es obligatorio")
    private PhaseDTO phase;

    @NotBlank(message = "El tipo de valor es obligatorio")
    private String valueType; // "NUMBER", "STRING", "BOOL", "DATE"

    @NotNull(message = "El campo mandatory es obligatorio")
    private Boolean mandatory;
}

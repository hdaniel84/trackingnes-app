package com.mesacer.trackingnes.trackingnes_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhaseDTO {

    private Long id;

    @NotBlank(message = "Obrigatorio")
    @Size(max = 50, message = "La descripción no puede superar 50 caracteres")
    private String description;
}

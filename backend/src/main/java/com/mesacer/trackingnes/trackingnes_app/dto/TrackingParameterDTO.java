package com.mesacer.trackingnes.trackingnes_app.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingParameterDTO {

    private Long id;

    private Long parameterId;

    private String parameterTypeDescription;

    private Long trackingId;

    private Double valueNumber;

    @Size(max = 5000, message = "El valor string no puede superar 5000 caracteres")
    private String valueString;

    private Boolean valueBool;

    private String valueDate; // Formato ISO yyyy-MM-dd'T'HH:mm:ss
}

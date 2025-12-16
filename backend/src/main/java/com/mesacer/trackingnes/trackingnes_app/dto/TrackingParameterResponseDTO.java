package com.mesacer.trackingnes.trackingnes_app.dto;

import lombok.Data;

@Data
public class TrackingParameterResponseDTO {

    private Long id;
    private Long parameterId;
    
    // Dato enriquecido: El nombre del parámetro para mostrar en la tabla sin hacer otra petición
    private String parameterDescription; 
    
    private String valueString;
    private Double valueNumber;
    private Boolean valueBool;
}
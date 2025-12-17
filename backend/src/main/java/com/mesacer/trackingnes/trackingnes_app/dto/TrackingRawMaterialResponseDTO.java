package com.mesacer.trackingnes.trackingnes_app.dto;

import lombok.Data;

@Data
public class TrackingRawMaterialResponseDTO {

    private Long id;
    private Long rawMaterialId;
    private String rawaMaterialDescription; 
    private String value;
}
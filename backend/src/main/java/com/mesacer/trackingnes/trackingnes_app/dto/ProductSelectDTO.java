package com.mesacer.trackingnes.trackingnes_app.dto;

import lombok.Data;

@Data
public class ProductSelectDTO {
    private Long id;
    private String productCode;       // Ej: FF001...
    private String description;
    private String shapeId;    // Ej: 001 (Vital para validación cruzada)
}
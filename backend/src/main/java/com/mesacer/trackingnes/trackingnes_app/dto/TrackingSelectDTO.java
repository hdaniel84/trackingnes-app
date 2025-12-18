package com.mesacer.trackingnes.trackingnes_app.dto;

import lombok.Data;

@Data
public class TrackingSelectDTO {
    private Long id;
    private String description; // Ej: "Taza (Lote 123) - 14:00"
    private Long productId;
    private String codigoProduto;

}
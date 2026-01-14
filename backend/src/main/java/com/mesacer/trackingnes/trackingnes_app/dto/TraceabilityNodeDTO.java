package com.mesacer.trackingnes.trackingnes_app.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class TraceabilityNodeDTO {
    // Claves requeridas por PrimeVue OrganizationChart
    private String key; // ID único (string)
    private NodeData data; // Datos visuales
    private List<TraceabilityNodeDTO> children = new ArrayList<>();

    // Configuración visual
    private boolean expanded = true;
    private String type = "person"; // 'person' es el template default, puedes cambiarlo
    private String styleClass;

    @Data
    public static class NodeData {
        private Long id;
        private String label; // Nombre de la Fase
        private String productDesc; // Descripción del producto
        private String productCode; // Código SAP/ERP
        private String quantity;
        private String quantityScrap;
        private String logisticUnit; // Carro/Palet
        private String date;
        private String time;
        private String team; // Nombre del equipo
        private String shift; // Turno
        private String styleClass;
    }
}
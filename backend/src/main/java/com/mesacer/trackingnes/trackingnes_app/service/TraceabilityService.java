package com.mesacer.trackingnes.trackingnes_app.service;

import com.mesacer.trackingnes.trackingnes_app.dto.TraceabilityNodeDTO;

public interface TraceabilityService {
    /**
     * Construye el árbol genealógico completo de ancestros para un tracking ID dado.
     * @param trackingId ID del registro hijo/nieto.
     * @return Nodo raíz (el registro solicitado) con sus padres/abuelos anidados.
     */
    TraceabilityNodeDTO getAncestryTree(Long trackingId);
}
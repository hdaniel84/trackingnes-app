package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import com.mesacer.trackingnes.trackingnes_app.dto.ParameterVidragemDTO;

public interface ParameterVidragemService {
    List<ParameterVidragemDTO> findAll();

    ParameterVidragemDTO findById(Long id);
}

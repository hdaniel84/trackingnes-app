package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;
import com.mesacer.trackingnes.trackingnes_app.dto.ParameterDTO;

public interface ParameterService {
    List<ParameterDTO> findAll();
    ParameterDTO findById(Long id);
}

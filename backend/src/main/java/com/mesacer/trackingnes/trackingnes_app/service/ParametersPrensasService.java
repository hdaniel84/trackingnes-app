package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;
import com.mesacer.trackingnes.trackingnes_app.dto.ParametersPrensasDTO;

public interface ParametersPrensasService {
    List<ParametersPrensasDTO> findAll();
    ParametersPrensasDTO findById(Long id);
}

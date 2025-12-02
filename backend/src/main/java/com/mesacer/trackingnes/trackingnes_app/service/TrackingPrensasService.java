package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingPrensasDTO;

public interface TrackingPrensasService {
    List<TrackingPrensasDTO> findAll();
    TrackingPrensasDTO findById(Long id);
    TrackingPrensasDTO save(TrackingPrensasDTO dto);
    void delete(Long id);
}

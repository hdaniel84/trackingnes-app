package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingPrensasDTO;

public interface TrackingPrensasService {
    List<TrackingPrensasDTO> findAll();
    Page<TrackingPrensasDTO> findAll(Pageable pageable);
    TrackingPrensasDTO findById(Long id);
    TrackingPrensasDTO save(TrackingPrensasDTO dto);
    void delete(Long id);
}

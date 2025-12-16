package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingFornoEntradaDTO;

public interface TrackingFornoEntradaService {

    List<TrackingFornoEntradaDTO> findAll();
    Page<TrackingFornoEntradaDTO> findAll(Pageable pageable);
    TrackingFornoEntradaDTO findById(Long id);
    TrackingFornoEntradaDTO save(TrackingFornoEntradaDTO dto);
    void delete(Long id);
}
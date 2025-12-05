package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingVidragemDTO;

public interface TrackingVidragemService {

    List<TrackingVidragemDTO> findAll();
    Page<TrackingVidragemDTO> findAll(Pageable pageable);
    TrackingVidragemDTO findById(Long id);
    TrackingVidragemDTO save(TrackingVidragemDTO dto);
    void delete(Long id);
}
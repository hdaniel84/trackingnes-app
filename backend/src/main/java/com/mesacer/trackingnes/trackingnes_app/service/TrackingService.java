package com.mesacer.trackingnes.trackingnes_app.service;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrackingService {

    List<TrackingResponseDTO> findAll();

    Page<TrackingResponseDTO> findAll(Pageable pageable);

    TrackingResponseDTO findById(Long id);

    TrackingResponseDTO create(TrackingRequestDTO request);

    TrackingResponseDTO update(Long id, TrackingRequestDTO request);

    void delete(Long id);
}
/* 
package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingDTO;

public interface TrackingService {

    List<TrackingDTO> findAll();

    Page<TrackingDTO> findAll(Pageable pageable);

    TrackingDTO findById(Long id);

    TrackingDTO save(TrackingDTO dto);

    void delete(Long id);
}
    */

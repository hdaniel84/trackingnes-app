package com.mesacer.trackingnes.trackingnes_app.service;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingSelectDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrackingService {

    Page<TrackingResponseDTO> getAll(Long phaseId, Pageable pageable);

    List<TrackingSelectDTO> findCandidates(List<Long> phaseIds, String referenceId, String filterType);

    TrackingResponseDTO findById(Long id);

    TrackingResponseDTO create(TrackingRequestDTO request);

    TrackingResponseDTO update(Long id, TrackingRequestDTO request);

    void delete(Long id);
}
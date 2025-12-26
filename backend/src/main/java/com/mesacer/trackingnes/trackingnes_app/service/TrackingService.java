package com.mesacer.trackingnes.trackingnes_app.service;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TrackingService {

    Page<TrackingResponseDTO> getAll(Pageable pageable, Long phaseId, Long productId, Long teamId, Long id, String logisticUnit, LocalDate date);

    List<TrackingResponseDTO> findCandidates(List<Long> phaseIds, String referenceId, String filterType);

    TrackingResponseDTO findById(Long id);

    TrackingResponseDTO create(TrackingRequestDTO request);

    TrackingResponseDTO update(Long id, TrackingRequestDTO request);

    void delete(Long id);
}
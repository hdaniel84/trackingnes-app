package com.mesacer.trackingnes.trackingnes_app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mesacer.trackingnes.trackingnes_app.model.Tracking;

public interface TrackingRepository extends JpaRepository<Tracking, Long> {
    Page<Tracking> findByPhaseId(Long phaseId, Pageable pageable);

    // Buscar por Fase, ordenado por fecha descendente
    List<Tracking> findTop100ByPhaseIdOrderByStartTimeDesc(Long phaseId);

}

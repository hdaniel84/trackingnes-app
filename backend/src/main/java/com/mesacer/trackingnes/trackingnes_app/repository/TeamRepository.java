package com.mesacer.trackingnes.trackingnes_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mesacer.trackingnes.trackingnes_app.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
    // Ejemplo: buscar equipos por sección
    // List<Team> findBySectionId(Long sectionId);
}

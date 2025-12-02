package com.mesacer.trackingnes.trackingnes_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mesacer.trackingnes.trackingnes_app.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
}

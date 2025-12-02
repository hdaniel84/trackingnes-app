package com.mesacer.trackingnes.trackingnes_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mesacer.trackingnes.trackingnes_app.model.Shift;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    // Ejemplo: buscar turnos por hora de inicio
    // List<Shift> findByStartTime(LocalTime startTime);
}

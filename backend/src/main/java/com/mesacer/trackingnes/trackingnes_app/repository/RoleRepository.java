package com.mesacer.trackingnes.trackingnes_app.repository;

import com.mesacer.trackingnes.trackingnes_app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // Puedes añadir métodos de búsqueda si los necesitas, pero JpaRepository ya es suficiente por ahora.
}
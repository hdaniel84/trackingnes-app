package com.mesacer.trackingnes.trackingnes_app.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mesacer.trackingnes.trackingnes_app.model.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    @Override
    @EntityGraph(attributePaths = { "section" })
    List<Equipment> findAll(Sort sort);
}

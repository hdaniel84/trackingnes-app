package com.mesacer.trackingnes.trackingnes_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mesacer.trackingnes.trackingnes_app.model.Parameter;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Long> {
}

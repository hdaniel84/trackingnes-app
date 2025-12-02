package com.mesacer.trackingnes.trackingnes_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mesacer.trackingnes.trackingnes_app.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
    // Ejemplo: buscar personas por número de empleado
    // Optional<Person> findByEmployeeNumber(String employeeNumber);
}

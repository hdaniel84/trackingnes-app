package com.mesacer.trackingnes.trackingnes_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mesacer.trackingnes.trackingnes_app.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // Método clave que Spring Security necesita para buscar un usuario por su nombre
    Optional<User> findByUsername(String username);
}
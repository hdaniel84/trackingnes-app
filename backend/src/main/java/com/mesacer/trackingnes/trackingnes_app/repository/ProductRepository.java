package com.mesacer.trackingnes.trackingnes_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mesacer.trackingnes.trackingnes_app.model.Product;


public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
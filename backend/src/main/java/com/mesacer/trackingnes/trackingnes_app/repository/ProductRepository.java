package com.mesacer.trackingnes.trackingnes_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.mesacer.trackingnes.trackingnes_app.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    // Para llenar el combo de productos: "Traeme todos los que empiecen por FF"
    List<Product> findByProductCodeStartingWith(String prefix);

    // O varios tipos (Ej: FF y FS para Escolha)
    @Query("SELECT p FROM Product p WHERE p.productCode LIKE :prefix1% OR p.productCode LIKE :prefix2%")
    List<Product> findByCodePrefixes(String prefix1, String prefix2);
}
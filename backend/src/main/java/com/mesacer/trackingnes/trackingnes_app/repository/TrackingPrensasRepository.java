package com.mesacer.trackingnes.trackingnes_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingPrensas;

public interface TrackingPrensasRepository extends JpaRepository<TrackingPrensas, Long> {
    Page<TrackingPrensas> findByProductCodigoProduto(String codigoProduto, Pageable pageable);

}

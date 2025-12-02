package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import com.mesacer.trackingnes.trackingnes_app.dto.RawMaterialDTO;

public interface RawMaterialService {
    public List<RawMaterialDTO> findAll();

    public RawMaterialDTO findById(Long id);

    public RawMaterialDTO save(RawMaterialDTO dto);

    public void delete(Long id);
}

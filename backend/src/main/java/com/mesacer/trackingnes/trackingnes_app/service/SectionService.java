package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;
import com.mesacer.trackingnes.trackingnes_app.dto.SectionDTO;

public interface SectionService {
    List<SectionDTO> findAll();
    SectionDTO findById(Long id);
}

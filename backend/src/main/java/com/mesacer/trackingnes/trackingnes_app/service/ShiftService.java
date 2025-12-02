package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;
import com.mesacer.trackingnes.trackingnes_app.dto.ShiftDTO;

public interface ShiftService {
    List<ShiftDTO> findAll();
    ShiftDTO findById(Long id);
}

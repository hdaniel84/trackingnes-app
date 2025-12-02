package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;
import com.mesacer.trackingnes.trackingnes_app.dto.TeamDTO;

public interface TeamService {
    List<TeamDTO> findAll();
    TeamDTO findById(Long id);

}

package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mesacer.trackingnes.trackingnes_app.dto.TeamDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.TeamMapper;
import com.mesacer.trackingnes.trackingnes_app.repository.TeamRepository;

@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository repository;
    private final TeamMapper mapper;

    public TeamServiceImpl(TeamRepository repository, TeamMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<TeamDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public TeamDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }


}

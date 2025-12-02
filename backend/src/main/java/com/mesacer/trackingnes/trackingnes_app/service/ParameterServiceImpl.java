package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mesacer.trackingnes.trackingnes_app.dto.ParameterDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.ParameterMapper;
import com.mesacer.trackingnes.trackingnes_app.repository.ParameterRepository;

@Service
public class ParameterServiceImpl implements ParameterService {

    private final ParameterRepository repository;
    private final ParameterMapper mapper;

    public ParameterServiceImpl(ParameterRepository repository, ParameterMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<ParameterDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public ParameterDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }
}

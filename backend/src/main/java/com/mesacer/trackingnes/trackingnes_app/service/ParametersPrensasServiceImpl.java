package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mesacer.trackingnes.trackingnes_app.dto.ParametersPrensasDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.ParametersPrensasMapper;
import com.mesacer.trackingnes.trackingnes_app.repository.ParametersPrensasRepository;

@Service
public class ParametersPrensasServiceImpl implements ParametersPrensasService {

    private final ParametersPrensasRepository repository;
    private final ParametersPrensasMapper mapper;

    public ParametersPrensasServiceImpl(ParametersPrensasRepository repository, ParametersPrensasMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<ParametersPrensasDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public ParametersPrensasDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }
}

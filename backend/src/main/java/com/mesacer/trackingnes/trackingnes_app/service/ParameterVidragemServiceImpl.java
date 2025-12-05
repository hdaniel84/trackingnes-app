package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.mesacer.trackingnes.trackingnes_app.dto.ParameterVidragemDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.ParameterVidragemMapper;
import com.mesacer.trackingnes.trackingnes_app.repository.ParameterVidragemRepository;

@Service
public class ParameterVidragemServiceImpl implements ParameterVidragemService {

    private final ParameterVidragemRepository repository;
    private final ParameterVidragemMapper mapper;

    public ParameterVidragemServiceImpl(ParameterVidragemRepository repository, ParameterVidragemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<ParameterVidragemDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public ParameterVidragemDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }
}

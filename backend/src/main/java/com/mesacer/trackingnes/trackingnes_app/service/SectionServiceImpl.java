package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mesacer.trackingnes.trackingnes_app.dto.SectionDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.SectionMapper;
import com.mesacer.trackingnes.trackingnes_app.repository.SectionRepository;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionRepository repository;
    private final SectionMapper mapper;

    public SectionServiceImpl(SectionRepository repository, SectionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<SectionDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public SectionDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }
}

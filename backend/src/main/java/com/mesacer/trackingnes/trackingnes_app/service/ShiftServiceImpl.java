package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mesacer.trackingnes.trackingnes_app.dto.ShiftDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.ShiftMapper;
import com.mesacer.trackingnes.trackingnes_app.repository.ShiftRepository;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository repository;
    private final ShiftMapper mapper;

    public ShiftServiceImpl(ShiftRepository repository, ShiftMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<ShiftDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public ShiftDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }
}

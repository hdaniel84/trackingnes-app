package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingPrensasDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.TrackingPrensasMapper;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingPrensas;
import com.mesacer.trackingnes.trackingnes_app.repository.TrackingPrensasRepository;

@Service
public class TrackingPrensasServiceImpl implements TrackingPrensasService {

    private final TrackingPrensasRepository repository;
    private final TrackingPrensasMapper mapper;

    public TrackingPrensasServiceImpl(TrackingPrensasRepository repository, TrackingPrensasMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<TrackingPrensasDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public TrackingPrensasDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }

    @Override
    public TrackingPrensasDTO save(TrackingPrensasDTO dto) {
        TrackingPrensas entity = mapper.toEntity(dto);
        TrackingPrensas saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

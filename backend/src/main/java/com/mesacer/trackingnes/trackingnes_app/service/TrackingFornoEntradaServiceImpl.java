package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingFornoEntradaDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.TrackingFornoEntradaMapper;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingFornoEntrada;
import com.mesacer.trackingnes.trackingnes_app.repository.TrackingFornoEntradaRepository;

@Service
public class TrackingFornoEntradaServiceImpl implements TrackingFornoEntradaService {

    private final TrackingFornoEntradaRepository repository;
    private final TrackingFornoEntradaMapper mapper;

    public TrackingFornoEntradaServiceImpl(TrackingFornoEntradaRepository repository, TrackingFornoEntradaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<TrackingFornoEntradaDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public Page<TrackingFornoEntradaDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDTO);
    }

    @Override
    public TrackingFornoEntradaDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }

    @Override
    public TrackingFornoEntradaDTO save(TrackingFornoEntradaDTO dto) {
        TrackingFornoEntrada entity = mapper.toEntity(dto);

        // asegurar relación bidireccional
        if (entity.getParameters() != null) {
            entity.getParameters().forEach(p -> p.setTrackingFornoEntrada(entity));
        }

        TrackingFornoEntrada saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

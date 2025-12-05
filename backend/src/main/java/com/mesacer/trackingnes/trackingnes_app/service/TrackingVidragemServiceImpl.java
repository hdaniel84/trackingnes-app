package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingVidragemDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.TrackingVidragemMapper;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingVidragem;
import com.mesacer.trackingnes.trackingnes_app.repository.TrackingVidragemRepository;

@Service
public class TrackingVidragemServiceImpl implements TrackingVidragemService {

    private final TrackingVidragemRepository repository;
    private final TrackingVidragemMapper mapper;

    public TrackingVidragemServiceImpl(TrackingVidragemRepository repository, TrackingVidragemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<TrackingVidragemDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public Page<TrackingVidragemDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDTO);
    }

    @Override
    public TrackingVidragemDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }

    @Override
    public TrackingVidragemDTO save(TrackingVidragemDTO dto) {
        TrackingVidragem entity = mapper.toEntity(dto);

        // asegurar relación bidireccional
        if (entity.getParameters() != null) {
            entity.getParameters().forEach(p -> p.setTrackingVidragem(entity));
        }

        TrackingVidragem saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

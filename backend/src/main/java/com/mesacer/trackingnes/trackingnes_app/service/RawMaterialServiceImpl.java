package com.mesacer.trackingnes.trackingnes_app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mesacer.trackingnes.trackingnes_app.dto.RawMaterialDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.RawMaterialMapper;
import com.mesacer.trackingnes.trackingnes_app.model.RawMaterial;
import com.mesacer.trackingnes.trackingnes_app.repository.RawMaterialRepository;

@Service
public class RawMaterialServiceImpl implements RawMaterialService {

    private final RawMaterialRepository repository;
    private final RawMaterialMapper mapper;

    public RawMaterialServiceImpl(RawMaterialRepository repository, RawMaterialMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<RawMaterialDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public RawMaterialDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }

    public RawMaterialDTO save(RawMaterialDTO dto) {
        RawMaterial entity = mapper.toEntity(dto);
        RawMaterial saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

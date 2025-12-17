package com.mesacer.trackingnes.trackingnes_app.service;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.TrackingMapper;
import com.mesacer.trackingnes.trackingnes_app.model.Tracking;
import com.mesacer.trackingnes.trackingnes_app.repository.TrackingRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackingServiceImpl implements TrackingService {

    private final TrackingRepository repository;
    private final TrackingMapper mapper;
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Page<TrackingResponseDTO> getAll(Long phaseId, Pageable pageable) {
        Page<Tracking> page;

        if (phaseId != null) {
            // Si hay filtro, usamos el método específico
            page = repository.findByPhaseId(phaseId, pageable);
        } else {
            // Si es null, traemos todo (comportamiento anterior)
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toResponseDTO);
    }

    @Override
    public TrackingResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Tracking no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public TrackingResponseDTO create(TrackingRequestDTO request) {
        Tracking entity = mapper.toEntity(request);

        if (entity.getParameters() != null) {
            entity.getParameters().forEach(param -> param.setTracking(entity));
        }
        if (entity.getRawMaterials() != null) {
            entity.getRawMaterials().forEach(param -> param.setTracking(entity));
        }

        // Usamos saveAndFlush + refresh
        Tracking saved = repository.saveAndFlush(entity);
        entityManager.refresh(saved);

        return mapper.toResponseDTO(saved);
    }

    @Override
    @Transactional // Escritura
    public TrackingResponseDTO update(Long id, TrackingRequestDTO request) {
        // 1. Buscar
        Tracking existingEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tracking não encontrado: " + id));

        // 2. MapStruct actualiza los campos (Aquí se crean los objetos huecos con
        // description=null)
        mapper.updateEntityFromDto(request, existingEntity);

        // 3. Relación bidireccional (Parámetros)
        if (existingEntity.getParameters() != null) {
            existingEntity.getParameters().forEach(param -> param.setTracking(existingEntity));
        }
        if (existingEntity.getRawMaterials() != null) {
            existingEntity.getRawMaterials().forEach(param -> param.setTracking(existingEntity));
        }

        // 4. GUARDADO Y REFRESCO (LA SOLUCIÓN)

        // A. Guardamos y hacemos FLUSH inmediato para que los cambios vayan a la BD
        Tracking saved = repository.saveAndFlush(existingEntity);

        // B. Forzamos a Hibernate a RELEER la entidad desde la BD.
        // Esto llenará los campos 'description' de Team, Product, etc.
        entityManager.refresh(saved);

        // 5. Convertir a DTO (Ahora saved ya tiene las descripciones completas)
        return mapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar, ID no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
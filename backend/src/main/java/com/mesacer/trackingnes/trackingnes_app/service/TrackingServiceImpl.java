package com.mesacer.trackingnes.trackingnes_app.service;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.TrackingMapper;
import com.mesacer.trackingnes.trackingnes_app.model.Tracking;
import com.mesacer.trackingnes.trackingnes_app.repository.TrackingRepository;
import com.mesacer.trackingnes.trackingnes_app.repository.specs.TrackingSpecifications;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public Page<TrackingResponseDTO> getAll(Pageable pageable, Long phaseId, Long productId, Long teamId, Long id,
            String logisticUnit, LocalDate date) {

        Specification<Tracking> spec = TrackingSpecifications.withDynamicFilter(phaseId, productId, teamId, id,
                logisticUnit, date);

        Page<Tracking> pageResult = repository.findAll(spec, pageable);



        return pageResult.map(mapper::toResponseDTO);
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

    @Override
    @Transactional(readOnly = true)
    public List<TrackingResponseDTO> findCandidates(List<Long> phaseIds, String referenceId, String filterType) {
        var limit = PageRequest.of(0, 100);

        // Por defecto usamos PRODUCT_CODE si no especifican, o lo que venga
        String type = (filterType != null) ? filterType : "PRODUCT_CODE";

        Long refProductId = null;
        String refShapeId = null;

        if (referenceId != null && !referenceId.isBlank()) {
            switch (type) {
                case "PRODUCT_ID":
                case "SHAPE_ID": // Si fuera numérico
                    try {
                        refProductId = Long.parseLong(referenceId);
                    } catch (NumberFormatException e) {
                        return List.of();
                    }
                    break;

                case "PRODUCT_CODE": // CASO TEXTO (Vidragem)
                case "SHAPE":
                    // Aquí pasamos "W099" directamente
                    refShapeId = referenceId;
                    break;
            }
        }

        return repository.findCandidates(phaseIds, refProductId, refShapeId, type, limit)
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

}
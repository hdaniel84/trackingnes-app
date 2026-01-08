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

        // Relaciones bidireccionales
        if (entity.getParameters() != null) {
            entity.getParameters().forEach(param -> param.setTracking(entity));
        }
        if (entity.getRawMaterials() != null) {
            entity.getRawMaterials().forEach(param -> param.setTracking(entity));
        }

        // Default Scrap
        if (entity.getQuantityScrap() == null) {
            entity.setQuantityScrap(0);
        }

        // -----------------------------------------------------------
        // 2. PROCESAR ORÍGENES (Consumo de materia prima con validación)
        // -----------------------------------------------------------
        if (request.getSources() != null && !request.getSources().isEmpty()) {

            for (TrackingRequestDTO.SourceItemDTO sourceItem : request.getSources()) {

                // A. Buscar el Lote Padre
                Tracking parent = repository.findById(sourceItem.getTrackingId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Origen no encontrado: " + sourceItem.getTrackingId()));

                // B. Calcular Disponibilidad usando la VISTA mapeada
                // Ya no hacemos cálculos manuales. La entidad 'TrackingAvailability' tiene el dato.
                
                Integer available = 0;
                
                if (parent.getAvailability() != null) {
                    available = parent.getAvailability().getRemainingQuantity();
                } else {
                    // Fallback por seguridad: Si la vista no cargó (caso raro), el disponible es el total
                    available = parent.getQuantity(); 
                }

                // C. Validar si alcanza (Todo en INTEGER)
                if (sourceItem.getQuantityUsed() > available) {
                    throw new IllegalArgumentException(
                            String.format("Stock insuficiente en el lote #%d (%s). Disponible: %d, Solicitado: %d",
                                    parent.getId(), 
                                    parent.getProduct().getDescription(), 
                                    available,
                                    sourceItem.getQuantityUsed()));
                }

                // D. Agregar la relación (Usando Integer)
                entity.addSource(parent, sourceItem.getQuantityUsed());
            }
        }

        Tracking saved = repository.saveAndFlush(entity);
        entityManager.refresh(saved); // Refrescamos para traer IDs y datos calculados

        return mapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public TrackingResponseDTO update(Long id, TrackingRequestDTO request) {
        Tracking existingEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tracking não encontrado: " + id));

        mapper.updateEntityFromDto(request, existingEntity);

        if (existingEntity.getParameters() != null) {
            existingEntity.getParameters().forEach(param -> param.setTracking(existingEntity));
        }
        if (existingEntity.getRawMaterials() != null) {
            existingEntity.getRawMaterials().forEach(param -> param.setTracking(existingEntity));
        }

        // NOTA: Si permites editar 'Sources' en update, deberías replicar la lógica de validación de stock aquí.
        // Por ahora, asumimos que 'mapper.updateEntityFromDto' maneja los campos simples.

        Tracking saved = repository.saveAndFlush(existingEntity);
        entityManager.refresh(saved);
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
        String type = (filterType != null) ? filterType : "PRODUCT_CODE";

        Long refProductId = null;
        String refShapeId = null;

        if (referenceId != null && !referenceId.isBlank()) {
            switch (type) {
                case "PRODUCT_ID":
                case "SHAPE_ID":
                    try {
                        refProductId = Long.parseLong(referenceId);
                    } catch (NumberFormatException e) {
                        return List.of();
                    }
                    break;
                case "PRODUCT_CODE":
                case "SHAPE":
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
package com.mesacer.trackingnes.trackingnes_app.service;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingParameterRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRawMaterialRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.TrackingMapper;
import com.mesacer.trackingnes.trackingnes_app.model.Parameter;
import com.mesacer.trackingnes.trackingnes_app.model.RawMaterial;
import com.mesacer.trackingnes.trackingnes_app.model.Tracking;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingComposition;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingParameter;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingRawMaterial;
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
                // Ya no hacemos cálculos manuales. La entidad 'TrackingAvailability' tiene el
                // dato.

                Integer available = 0;

                if (parent.getAvailability() != null) {
                    available = parent.getAvailability().getRemainingQuantity();
                } else {
                    // Fallback por seguridad: Si la vista no cargó (caso raro), el disponible es el
                    // total
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

        validateMassBalance(entity);

        Tracking saved = repository.saveAndFlush(entity);
        entityManager.refresh(saved);

        // Refrescar padres también aquí por si acaso
        if (saved.getSourceComposition() != null) {
            for (TrackingComposition composition : saved.getSourceComposition()) {
                Tracking parent = composition.getParent();
                if (parent != null) {
                    entityManager.refresh(parent);

                    if (parent.getAvailability() != null) {
                        entityManager.refresh(parent.getAvailability());
                    }
                }
            }
        }

        return mapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public TrackingResponseDTO update(Long id, TrackingRequestDTO request) {
        // 1. Buscar Entidad (Managed)
        Tracking existingEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tracking não encontrado: " + id));

        // 2. Actualizar campos simples
        // IMPORTANTE: El Mapper debe tener ignore=true en las listas
        mapper.updateEntityFromDto(request, existingEntity);

        // 3. ACTUALIZAR LISTAS HIJAS MANUALMENTE

        // 3.1 Raw Materials
        if (request.getRawMaterials() != null) {
            updateRawMaterials(existingEntity, request.getRawMaterials());
        }

        // 3.2 Parameters
        if (request.getParameters() != null) {
            updateParameters(existingEntity, request.getParameters());
        }

        // 3.3 Sources
        if (request.getSources() != null) {
            updateSources(existingEntity, request.getSources());
        }

        // 4. VALIDACIÓN DE INTEGRIDAD DOWNSTREAM (Validación de hijos)
        // Obtenemos cuanto han gastado los hijos de este tracking
        Integer totalUsedByChildren = repository.getUsedQuantityByParent(id);

        if (existingEntity.getQuantity() < totalUsedByChildren) {
            throw new IllegalArgumentException(
                    String.format(
                            "Não é possível atualizar a quantidade para %d. Este lote já tem %d unidades consumidas por processos seguintes (Restante ficaria negativo).",
                            existingEntity.getQuantity(),
                            totalUsedByChildren));
        }

        // 5. Validar Balance de Masa
        validateMassBalance(existingEntity);

        // 6. Guardar
        Tracking saved = repository.saveAndFlush(existingEntity);
        entityManager.refresh(saved);
        refreshParents(saved);

        return mapper.toResponseDTO(saved);
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================

    private void updateRawMaterials(Tracking entity, List<TrackingRawMaterialRequestDTO> dtos) {
        // A. IDs entrantes
        List<Long> incomingIds = dtos.stream()
                .map(TrackingRawMaterialRequestDTO::getId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        // B. Eliminar huérfanos
        entity.getRawMaterials().removeIf(rm -> rm.getId() != null && !incomingIds.contains(rm.getId()));

        // C. Agregar o Actualizar
        for (TrackingRawMaterialRequestDTO dto : dtos) {

            // Obtenemos la referencia del tipo de material usando EntityManager
            // Esto evita una consulta SELECT si solo necesitamos asignar la FK
            RawMaterial rawMaterialRef = entityManager.getReference(RawMaterial.class, dto.getRawMaterialId());

            if (dto.getId() == null) {
                // NUEVO
                TrackingRawMaterial newRm = new TrackingRawMaterial();
                newRm.setRawMaterialType(rawMaterialRef); // ✅ CORREGIDO: Asignamos la Entidad, no el ID
                newRm.setValue(dto.getValue());
                newRm.setTracking(entity);
                entity.getRawMaterials().add(newRm);
            } else {
                // ACTUALIZAR
                entity.getRawMaterials().stream()
                        .filter(rm -> rm.getId().equals(dto.getId()))
                        .findFirst()
                        .ifPresent(existingRm -> {
                            existingRm.setRawMaterialType(rawMaterialRef); // ✅ CORREGIDO
                            existingRm.setValue(dto.getValue());
                        });
            }
        }
    }

    private void updateParameters(Tracking entity, List<TrackingParameterRequestDTO> dtos) {
        List<Long> incomingIds = dtos.stream()
                .map(TrackingParameterRequestDTO::getId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        entity.getParameters().removeIf(p -> p.getId() != null && !incomingIds.contains(p.getId()));

        for (TrackingParameterRequestDTO dto : dtos) {

            // Obtenemos la referencia del Parametro definición
            Parameter parameterRef = entityManager.getReference(Parameter.class, dto.getParameterId());

            if (dto.getId() == null) {
                // NUEVO
                TrackingParameter newParam = new TrackingParameter();
                newParam.setParameter(parameterRef); // ✅ CORREGIDO: Asignamos la Entidad
                newParam.setValueString(dto.getValueString());
                newParam.setValueNumber(dto.getValueNumber());
                newParam.setValueBool(dto.getValueBool());
                newParam.setValueDate(dto.getValueDate());
                newParam.setTracking(entity);
                entity.getParameters().add(newParam);
            } else {
                // ACTUALIZAR
                entity.getParameters().stream()
                        .filter(p -> p.getId().equals(dto.getId()))
                        .findFirst()
                        .ifPresent(existingP -> {
                            existingP.setParameter(parameterRef); // ✅ CORREGIDO
                            existingP.setValueString(dto.getValueString());
                            existingP.setValueNumber(dto.getValueNumber());
                            existingP.setValueBool(dto.getValueBool());
                            existingP.setValueDate(dto.getValueDate());
                        });
            }
        }
    }

    private void updateSources(Tracking existingEntity, List<TrackingRequestDTO.SourceItemDTO> sources) {
        List<Long> newSourceIds = sources.stream()
                .map(TrackingRequestDTO.SourceItemDTO::getTrackingId)
                .collect(Collectors.toList());

        existingEntity.getSourceComposition().removeIf(comp -> !newSourceIds.contains(comp.getParent().getId()));

        for (TrackingRequestDTO.SourceItemDTO sourceDto : sources) {

            TrackingComposition existingLink = existingEntity.getSourceComposition().stream()
                    .filter(c -> c.getParent().getId().equals(sourceDto.getTrackingId()))
                    .findFirst()
                    .orElse(null);

            if (existingLink != null) {
                Integer oldQty = existingLink.getQuantityUsed();
                Integer newQty = sourceDto.getQuantityUsed();

                if (newQty > oldQty) {
                    Integer diff = newQty - oldQty;
                    validateParentAvailability(existingLink.getParent(), diff, true);
                }
                existingLink.setQuantityUsed(newQty);
            } else {
                Tracking parent = repository.findById(sourceDto.getTrackingId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Origen no encontrado: " + sourceDto.getTrackingId()));

                validateParentAvailability(parent, sourceDto.getQuantityUsed(), false);
                existingEntity.addSource(parent, sourceDto.getQuantityUsed());
            }
        }
    }

    // Helper para validar stock disponible en el Padre (Upstream)
    private void validateParentAvailability(Tracking parent, Integer requiredAmount, boolean isIncrement) {
        Integer available = 0;
        if (parent.getAvailability() != null) {
            available = parent.getAvailability().getRemainingQuantity();
        } else {
            available = parent.getQuantity(); // Fallback
        }

        if (requiredAmount > available) {
            String msg = isIncrement
                    ? "Stock insuficiente no lote %d para o aumento. Disponível: %d, Aumento: %d"
                    : "Stock insuficiente no lote %d. Disponível: %d, Solicitado: %d";

            throw new IllegalArgumentException(String.format(msg, parent.getId(), available, requiredAmount));
        }
    }

    // Helper para refrescar contexto de persistencia
    private void refreshParents(Tracking saved) {
        if (saved.getSourceComposition() != null) {
            for (TrackingComposition composition : saved.getSourceComposition()) {
                Tracking parent = composition.getParent();
                if (parent != null) {
                    entityManager.refresh(parent);
                    if (parent.getAvailability() != null) {
                        entityManager.refresh(parent.getAvailability());
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Não se pode remover, ID não encontrado: " + id);
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

    private void validateMassBalance(Tracking entity) {
        if (entity.getSourceComposition() == null || entity.getSourceComposition().isEmpty()) {
            return;
        }

        // 1. Validar que ninguna cantidad sea negativa (Seguridad extra)
        if ((entity.getQuantity() != null && entity.getQuantity() < 0) ||
                (entity.getQuantityScrap() != null && entity.getQuantityScrap() < 0) ||
                (entity.getQuantitySecond() != null && entity.getQuantitySecond() < 0) ||
                (entity.getQuantityRework() != null && entity.getQuantityRework() < 0)) {
            throw new IllegalArgumentException("As quantidades não podem ser valores negativos.");
        }

        // 2. Calcular Total Salida (Output)
        int qtyOk = entity.getQuantity() != null ? entity.getQuantity() : 0;
        int qtyScrap = entity.getQuantityScrap() != null ? entity.getQuantityScrap() : 0;
        int qtySecond = entity.getQuantitySecond() != null ? entity.getQuantitySecond() : 0;
        int qtyRework = entity.getQuantityRework() != null ? entity.getQuantityRework() : 0;

        int totalOutput = qtyOk + qtyScrap + qtySecond + qtyRework;

        // 3. Calcular Total Entrada (Input)
        int totalInput = entity.getSourceComposition().stream()
                .mapToInt(TrackingComposition::getQuantityUsed)
                .sum();

        // 4. Validar
        if (totalOutput > totalInput) {
            int diff = totalOutput - totalInput;
            throw new IllegalArgumentException(
                    String.format(
                            "Inconsistência de Balanço: A produção total (%d) [OK:%d + Scrap:%d + 2ª:%d + Retoque:%d] excede a entrada total (%d) em %d unidades.",
                            totalOutput, qtyOk, qtyScrap, qtySecond, qtyRework, totalInput, diff));
        }
    }
}
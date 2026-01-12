package com.mesacer.trackingnes.trackingnes_app.service;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.TrackingMapper;
import com.mesacer.trackingnes.trackingnes_app.model.Tracking;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingComposition;
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
        // 1. Buscar Entidad Existente
        Tracking existingEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tracking não encontrado: " + id));

        // 2. Actualizar campos simples (MapStruct)
        mapper.updateEntityFromDto(request, existingEntity);

        // 3. ACTUALIZACIÓN MANUAL DE ORÍGENES (Sources)
        if (request.getSources() != null) {

            // A. Limpiar orígenes eliminados
            List<Long> newSourceIds = request.getSources().stream()
                    .map(TrackingRequestDTO.SourceItemDTO::getTrackingId)
                    .collect(Collectors.toList());

            existingEntity.getSourceComposition().removeIf(comp -> !newSourceIds.contains(comp.getParent().getId()));

            // B. Actualizar o Agregar nuevos
            for (TrackingRequestDTO.SourceItemDTO sourceDto : request.getSources()) {

                // Buscamos si ya existe esta relación
                TrackingComposition existingLink = existingEntity.getSourceComposition().stream()
                        .filter(c -> c.getParent().getId().equals(sourceDto.getTrackingId()))
                        .findFirst()
                        .orElse(null);

                if (existingLink != null) {
                    // CASO 1: ACTUALIZAR CANTIDAD (Validación Robusta)
                    Integer oldQty = existingLink.getQuantityUsed();
                    Integer newQty = sourceDto.getQuantityUsed();

                    // Solo validamos si estamos PIDIENDO MÁS material
                    if (newQty > oldQty) {
                        Integer diff = newQty - oldQty;

                        // Obtenemos el stock disponible actual del padre desde la BD
                        Tracking parent = existingLink.getParent();
                        Integer availableInDb = 0;
                        if (parent.getAvailability() != null) {
                            availableInDb = parent.getAvailability().getRemainingQuantity();
                        } else {
                            // Fallback (debería tener availability si usas la vista)
                            // Si no hay vista cargada, forzamos recarga o cálculo manual.
                            // Aquí asumimos seguridad: si no carga availability, quizás no hay stock
                            // seguro.
                            availableInDb = parent.getQuantity(); // OJO: Esto podría ser inseguro si no restas lo usado
                                                                  // por otros.
                        }

                        if (diff > availableInDb) {
                            throw new IllegalArgumentException(
                                    String.format(
                                            "Stock insuficiente no lote %d para o aumento solicitado. Disponível: %d, Aumento: %d",
                                            parent.getId(), availableInDb, diff));
                        }
                    }

                    // Si pasa la validación (o si diff <= 0), actualizamos
                    existingLink.setQuantityUsed(newQty);

                } else {
                    // CASO 2: NUEVA RELACIÓN (Validación Estándar)
                    Tracking parent = repository.findById(sourceDto.getTrackingId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Origen no encontrado: " + sourceDto.getTrackingId()));

                    Integer available = 0;
                    if (parent.getAvailability() != null) {
                        available = parent.getAvailability().getRemainingQuantity();
                    } else {
                        available = parent.getQuantity();
                    }

                    if (sourceDto.getQuantityUsed() > available) {
                        throw new IllegalArgumentException(
                                String.format(
                                        "Stock insuficiente para adicionar um novo lote %d. Disponível: %d, Solicitado: %d",
                                        parent.getId(), available, sourceDto.getQuantityUsed()));
                    }

                    existingEntity.addSource(parent, sourceDto.getQuantityUsed());
                }
            }
        }

        // 4. Relaciones bidireccionales
        if (existingEntity.getParameters() != null) {
            existingEntity.getParameters().forEach(param -> param.setTracking(existingEntity));
        }
        if (existingEntity.getRawMaterials() != null) {
            existingEntity.getRawMaterials().forEach(param -> param.setTracking(existingEntity));
        }

        validateMassBalance(existingEntity);
        Tracking saved = repository.saveAndFlush(existingEntity);
        entityManager.refresh(saved);

        // 3. REFRESCAR LOS PADRES Y SU DISPONIBILIDAD
        if (saved.getSourceComposition() != null) {
            for (TrackingComposition composition : saved.getSourceComposition()) {
                Tracking parent = composition.getParent();
                if (parent != null) {
                    // A. Refrescamos al Padre (Tracking)
                    entityManager.refresh(parent);

                    // B. Refrescamos explícitamente la Vista de Disponibilidad
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

    /**
     * Valida que la producción total (OK + Scrap) no exceda el consumo total de los
     * orígenes.
     */
    private void validateMassBalance(Tracking entity) {
        // 1. Si no hay orígenes, no hay balance de masa contra lotes anteriores que validar.
        if (entity.getSourceComposition() == null || entity.getSourceComposition().isEmpty()) {
            return;
        }

        // 2. Calcular Total Salida (Output)
        int qtyOk = entity.getQuantity() != null ? entity.getQuantity() : 0;
        int qtyScrap = entity.getQuantityScrap() != null ? entity.getQuantityScrap() : 0;
        int totalOutput = qtyOk + qtyScrap;

        // 3. Calcular Total Entrada (Input)
        int totalInput = entity.getSourceComposition().stream()
                .mapToInt(TrackingComposition::getQuantityUsed)
                .sum();

        // 4. Validar: La Salida NO puede ser mayor que la Entrada
        if (totalOutput > totalInput) {
            int diff = totalOutput - totalInput;
            throw new IllegalArgumentException(
                    String.format(
                            "Inconsistência de Balanço: A produção total (%d) excede a entrada total (%d) em %d unidades.",
                            totalOutput, totalInput, diff));
        }
    }
}
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrackingServiceImpl implements TrackingService {

    private final TrackingRepository repository;
    private final TrackingMapper mapper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TrackingResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    public Page<TrackingResponseDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponseDTO);
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
                .orElseThrow(() -> new EntityNotFoundException("Tracking no encontrado: " + id));

        // 2. MapStruct actualiza los campos (Aquí se crean los objetos huecos con
        // description=null)
        mapper.updateEntityFromDto(request, existingEntity);

        // 3. Relación bidireccional (Parámetros)
        if (existingEntity.getParameters() != null) {
            existingEntity.getParameters().forEach(param -> param.setTracking(existingEntity));
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

/*
 * package com.mesacer.trackingnes.trackingnes_app.service;
 * 
 * import java.util.List;
 * 
 * import org.springframework.data.domain.Page;
 * import org.springframework.data.domain.Pageable;
 * import org.springframework.stereotype.Service;
 * 
 * import com.mesacer.trackingnes.trackingnes_app.dto.TrackingDTO;
 * import com.mesacer.trackingnes.trackingnes_app.mapper.TrackingMapper;
 * import com.mesacer.trackingnes.trackingnes_app.model.Tracking;
 * import com.mesacer.trackingnes.trackingnes_app.repository.TrackingRepository;
 * 
 * @Service
 * public class TrackingServiceImpl implements TrackingService {
 * 
 * private final TrackingRepository repository;
 * private final TrackingMapper mapper;
 * 
 * public TrackingServiceImpl(TrackingRepository repository, TrackingMapper
 * mapper) {
 * this.repository = repository;
 * this.mapper = mapper;
 * }
 * 
 * @Override
 * public List<TrackingDTO> findAll() {
 * return repository.findAll()
 * .stream()
 * .map(mapper::toDTO)
 * .toList();
 * }
 * 
 * @Override
 * public Page<TrackingDTO> findAll(Pageable pageable) {
 * return repository.findAll(pageable)
 * .map(mapper::toDTO);
 * }
 * 
 * @Override
 * public TrackingDTO findById(Long id) {
 * return repository.findById(id)
 * .map(mapper::toDTO)
 * .orElse(null);
 * }
 * 
 * @Override
 * public TrackingDTO save(TrackingDTO dto) {
 * Tracking entity = mapper.toEntity(dto);
 * 
 * // asegurar relación bidireccional
 * if (entity.getParameters() != null) {
 * entity.getParameters().forEach(p -> p.setTracking(entity));
 * }
 * 
 * if (entity.getRawMaterials() != null) {
 * entity.getRawMaterials().forEach(p -> p.setTracking(entity));
 * }
 * 
 * Tracking saved = repository.save(entity);
 * return mapper.toDTO(saved);
 * }
 * 
 * @Override
 * public void delete(Long id) {
 * repository.deleteById(id);
 * }
 * }
 */
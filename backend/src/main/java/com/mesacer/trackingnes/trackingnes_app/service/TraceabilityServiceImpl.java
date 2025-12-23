package com.mesacer.trackingnes.trackingnes_app.service;

import com.mesacer.trackingnes.trackingnes_app.dto.TraceabilityNodeDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.TraceabilityMapper; // Inyectamos el Mapper
import com.mesacer.trackingnes.trackingnes_app.model.Tracking;
import com.mesacer.trackingnes.trackingnes_app.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraceabilityServiceImpl implements TraceabilityService {

    private final TrackingRepository repository;
    private final TraceabilityMapper traceabilityMapper; // 🚀 Inyección

    @Override
    @Transactional(readOnly = true)
    public TraceabilityNodeDTO getAncestryTree(Long trackingId) {
        // 1. Obtener IDs (Query Nativa CTE)
        List<Long> ancestorIds = repository.findAncestorIds(trackingId);

        if (ancestorIds == null || ancestorIds.isEmpty()) {
            return repository.findById(trackingId)
                    .map(traceabilityMapper::toDTO) // Uso directo del mapper
                    .orElse(null);
        }

        // 2. Cargar entidades
        List<Tracking> flatList = repository.findAllById(ancestorIds);
        Map<Long, Tracking> entityMap = flatList.stream()
                .collect(Collectors.toMap(Tracking::getId, t -> t));

        // 3. Buscar Raíz
        Tracking rootEntity = entityMap.get(trackingId);
        if (rootEntity == null)
            return null;

        // 4. Construir Recursivamente
        return buildRecursiveTree(rootEntity, entityMap);
    }

    // El algoritmo estructural se queda aquí, pero la creación del objeto la hace
    // el Mapper
    private TraceabilityNodeDTO buildRecursiveTree(Tracking current, Map<Long, Tracking> allNodes) {

        // MapStruct crea el nodo con todos los datos y estilos
        TraceabilityNodeDTO node = traceabilityMapper.toDTO(current);

        // Lógica de grafo: Buscar al PADRE (Tracking Source) y añadirlo como hijo
        // visual
        if (current.getTrackingSource() != null) {
            Long parentId = current.getTrackingSource().getId();
            Tracking parentEntity = allNodes.get(parentId);

            if (parentEntity != null) {
                // Recursión
                node.getChildren().add(buildRecursiveTree(parentEntity, allNodes));
            }
        }

        return node;
    }
}
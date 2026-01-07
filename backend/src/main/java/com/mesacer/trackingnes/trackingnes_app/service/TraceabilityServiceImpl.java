package com.mesacer.trackingnes.trackingnes_app.service;

import com.mesacer.trackingnes.trackingnes_app.dto.TraceabilityNodeDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.TraceabilityMapper;
import com.mesacer.trackingnes.trackingnes_app.model.Tracking;
import com.mesacer.trackingnes.trackingnes_app.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraceabilityServiceImpl implements TraceabilityService {

    private final TrackingRepository repository;
    private final TraceabilityMapper traceabilityMapper;

    @Override
    @Transactional(readOnly = true)
    public TraceabilityNodeDTO getAncestryTree(Long trackingId) {

        // 1. Obtener IDs de toda la genealogía (Padres, Abuelos...) usando la Query
        // Recursiva optimizada
        List<Long> allNodeIds = repository.findAncestorIds(trackingId);

        // IMPORTANTE: La query recursiva devuelve ancestros, pero necesitamos añadir
        // al nodo raíz (el hijo actual) para poder empezar a dibujar.
        if (allNodeIds == null) {
            allNodeIds = new ArrayList<>();
        }
        allNodeIds.add(trackingId);

        // 2. Cargar todas las entidades de golpe (Evita el problema N+1 queries)
        List<Tracking> flatList = repository.findAllById(allNodeIds);

        if (flatList.isEmpty()) {
            return null;
        }

        // Convertimos a un Map para acceso instantáneo por ID durante la recursión
        Map<Long, Tracking> entityMap = flatList.stream()
                .collect(Collectors.toMap(Tracking::getId, t -> t));

        // 3. Buscar el nodo Raíz en el mapa
        Tracking rootEntity = entityMap.get(trackingId);
        if (rootEntity == null) {
            return null; // Caso raro de inconsistencia
        }

        // 4. Construir Recursivamente
        // Usamos un "Set" de visitados para evitar ciclos visuales,
        // pero por ahora confiaremos en la estructura de árbol
        return buildRecursiveTree(rootEntity, entityMap);
    }

    /**
     * Construye el árbol visual.
     * En una estructura Many-to-Many, un nodo puede tener múltiples padres.
     */
    private TraceabilityNodeDTO buildRecursiveTree(Tracking current, Map<Long, Tracking> allNodesMap) {

        // 1. Mapear el nodo actual (Datos visuales)
        TraceabilityNodeDTO node = traceabilityMapper.toDTO(current);

        // 2. Lógica de Red: Iterar sobre la LISTA de padres (sourceTrackings)
        // JPA cargará la colección perezosa (Lazy), pero como estamos en @Transactional
        // funciona.
        if (current.getSourceTrackings() != null && !current.getSourceTrackings().isEmpty()) {

            for (Tracking parentStub : current.getSourceTrackings()) {

                // Obtenemos la entidad completa de nuestro Mapa precargado
                // (El parentStub dentro de la lista podría ser un Proxy sin datos)
                Tracking fullParentEntity = allNodesMap.get(parentStub.getId());

                if (fullParentEntity != null) {
                    // Recursión: Construir el sub-árbol de este padre y añadirlo como hijo visual
                    TraceabilityNodeDTO childNode = buildRecursiveTree(fullParentEntity, allNodesMap);
                    node.getChildren().add(childNode);
                }
            }
        }

        return node;
    }
}
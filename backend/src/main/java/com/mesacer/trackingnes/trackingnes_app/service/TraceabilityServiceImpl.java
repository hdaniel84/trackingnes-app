package com.mesacer.trackingnes.trackingnes_app.service;

import com.mesacer.trackingnes.trackingnes_app.dto.TraceabilityNodeDTO;
import com.mesacer.trackingnes.trackingnes_app.mapper.TraceabilityMapper;
import com.mesacer.trackingnes.trackingnes_app.model.Tracking;
import com.mesacer.trackingnes.trackingnes_app.model.TrackingComposition; // Importante
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

        // 1. Obtener IDs (Esto no cambia, la query nativa ya buscaba en
        // tracking_composition)
        List<Long> allNodeIds = repository.findAncestorIds(trackingId);

        if (allNodeIds == null) {
            allNodeIds = new ArrayList<>();
        }
        allNodeIds.add(trackingId);

        // 2. Cargar entidades (Esto no cambia, seguimos cargando objetos Tracking)
        List<Tracking> flatList = repository.findAllById(allNodeIds);

        if (flatList.isEmpty()) {
            return null;
        }

        // Mapa para acceso rápido
        Map<Long, Tracking> entityMap = flatList.stream()
                .collect(Collectors.toMap(Tracking::getId, t -> t));

        // 3. Buscar Raíz
        Tracking rootEntity = entityMap.get(trackingId);
        if (rootEntity == null) {
            return null;
        }

        // 4. Construir Recursivamente
        return buildRecursiveTree(rootEntity, entityMap);
    }

    /**
     * Construye el árbol visual navegando la entidad intermedia.
     */
    private TraceabilityNodeDTO buildRecursiveTree(Tracking current, Map<Long, Tracking> allNodesMap) {

        // 1. Mapear nodo actual
        TraceabilityNodeDTO node = traceabilityMapper.toDTO(current);

        // 2. Lógica de Red con Entidad Intermedia
        // Ahora iteramos sobre 'sourceComposition' en lugar de 'sourceTrackings'
        if (current.getSourceComposition() != null && !current.getSourceComposition().isEmpty()) {

            for (TrackingComposition composition : current.getSourceComposition()) {

                // A. Obtenemos el Stub del padre desde la relación
                Tracking parentStub = composition.getParent();

                // B. Buscamos el objeto completo en nuestro mapa precargado
                // (Usamos el ID del padre para buscarlo en el Map)
                Tracking fullParentEntity = allNodesMap.get(parentStub.getId());

                if (fullParentEntity != null) {

                    // C. Recursión
                    TraceabilityNodeDTO childNode = buildRecursiveTree(fullParentEntity, allNodesMap);

                    // OPCIONAL: Para mostrar cuánto se usó en el árbol visual,
                    // inyectarlo aquí en el DTO del hijo.
                    // Ej: childNode.getData().setUsedQty(composition.getQuantityUsed() + " kg");

                    node.getChildren().add(childNode);
                }
            }
        }

        return node;
    }
}
package com.mesacer.trackingnes.trackingnes_app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mesacer.trackingnes.trackingnes_app.model.Tracking;

public interface TrackingRepository extends JpaRepository<Tracking, Long> {
        Page<Tracking> findByPhaseId(Long phaseId, Pageable pageable);

        // Buscar por Fase, ordenado por fecha descendente
        List<Tracking> findTop100ByPhaseIdOrderByStartTimeDesc(Long phaseId);

        @Query("SELECT t FROM Tracking t WHERE t.phase.id IN :phaseIds " +
                        "AND (" +
                        "(:filterType IS NULL) OR " +

                        // CASO 1: ID Numérico (Correcto: Long vs Long)
                        "(:filterType = 'PRODUCT_ID' AND t.product.id = :refProductId) OR " +

                        // CASO 2: SHAPE (CORREGIDO: String vs String)
                        // Asumiendo que en tu entidad Product el campo se llama 'shape' o 'shapeCode' y
                        // es String
                        // Y lo comparamos contra el parámetro de TEXTO (:refShapeId), no el numérico.
                        "(:filterType = 'SHAPE' AND t.product.shape.id = :refShapeId) OR " +

                        // CASO 3: PRODUCT_CODE (Correcto: String vs String)
                        "(:filterType = 'PRODUCT_CODE' AND t.product.productCode = :refShapeId)" +
                        ") " +
                        "ORDER BY t.startTime DESC")
        List<Tracking> findCandidates(@Param("phaseIds") List<Long> phaseIds,
                        @Param("refProductId") Long refProductId,
                        @Param("refShapeId") String refShapeId,
                        @Param("filterType") String filterType,
                        Pageable pageable);

        @Query(value = """
                            WITH RECURSIVE genealogy AS (
                                -- 1. El punto de partida (El hijo/nieto actual)
                                SELECT
                                    t.id, t.id_tracking_source, t.id_phase, t.id_product, t.quantity, t.start_time, 0 as depth
                                FROM tracking t
                                WHERE t.id = :trackingId

                                UNION ALL

                                -- 2. La recursión (Buscamos al padre/origen)
                                SELECT
                                    parent.id, parent.id_tracking_source, parent.id_phase, parent.id_product, parent.quantity, parent.start_time, g.depth + 1
                                FROM tracking parent
                                INNER JOIN genealogy g ON g.id_tracking_source = parent.id
                            )
                            SELECT * FROM genealogy;
                            -- Nota: JPA necesita mapear esto a Entidades.
                            -- Para evitar problemas de mapeo nativo complejo, lo mejor es devolver solo IDs
                            -- y luego hacer un findAllById, O usar una proyección.
                            -- Opción Enterprise Robusta: Fetch de IDs y luego Fetch de Entidades.
                        """, nativeQuery = true)
        List<Long> findAncestorIds(@Param("trackingId") Long trackingId);

}

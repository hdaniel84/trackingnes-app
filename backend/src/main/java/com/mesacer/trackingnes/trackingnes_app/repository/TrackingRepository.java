package com.mesacer.trackingnes.trackingnes_app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mesacer.trackingnes.trackingnes_app.model.Tracking;

public interface TrackingRepository extends JpaRepository<Tracking, Long>, JpaSpecificationExecutor<Tracking> {

    Page<Tracking> findByPhaseId(Long phaseId, Pageable pageable);

    List<Tracking> findTop100ByPhaseIdOrderByStartTimeDesc(Long phaseId);

    // --- QUERY CANDIDATES (JPQL) ---
    @Query("""
                SELECT t
                FROM Tracking t
                JOIN t.availability a
                WHERE t.phase.id IN :phaseIds
                AND a.remainingQuantity > 0
                AND (
                    (:filterType IS NULL) OR
                    (:filterType = 'PRODUCT_ID' AND t.product.id = :refProductId) OR
                    (:filterType = 'SHAPE' AND t.product.shape.id = :refShapeId) OR
                    (:filterType = 'PRODUCT_CODE' AND t.product.productCode = :refShapeId)
                )
                ORDER BY t.startTime DESC
            """)
    List<Tracking> findCandidates(
            @Param("phaseIds") List<Long> phaseIds,
            @Param("refProductId") Long refProductId,
            @Param("refShapeId") String refShapeId,
            @Param("filterType") String filterType,
            Pageable pageable);

    // --- NUEVA QUERY RECURSIVA (Native SQL para Redes) ---
    // Devuelve todos los IDs de los lotes origen (padres, abuelos, bisabuelos...)
    @Query(value = """
                WITH RECURSIVE genealogy AS (
                    -- 1. Punto de partida: Buscamos los padres directos del tracking actual
                    SELECT
                        tc.id_parent,
                        1 as depth
                    FROM tracking_composition tc
                    WHERE tc.id_child = :trackingId

                    UNION ALL

                    -- 2. Recursión: Buscamos los padres de esos padres (abuelos)
                    SELECT
                        tc.id_parent,
                        g.depth + 1
                    FROM tracking_composition tc
                    INNER JOIN genealogy g ON tc.id_child = g.id_parent
                    -- Evitamos ciclos infinitos limitando profundidad o detectando bucles (opcional)
                    WHERE g.depth < 20
                )
                SELECT DISTINCT id_parent FROM genealogy
            """, nativeQuery = true)
    List<Long> findAncestorIds(@Param("trackingId") Long trackingId);

    // --- QUERY INVERSA (Opcional pero útil) ---
    // "Forward Traceability": ¿A dónde fue a parar este lote? (Hijos, Nietos)
    @Query(value = """
                WITH RECURSIVE forward_trace AS (
                    SELECT tc.id_child, 1 as depth
                    FROM tracking_composition tc
                    WHERE tc.id_parent = :trackingId

                    UNION ALL

                    SELECT tc.id_child, ft.depth + 1
                    FROM tracking_composition tc
                    INNER JOIN forward_trace ft ON tc.id_parent = ft.id_child
                )
                SELECT DISTINCT id_child FROM forward_trace
            """, nativeQuery = true)
    List<Long> findDescendantIds(@Param("trackingId") Long trackingId);

    // CALCULAR CONSUMO TOTAL DE UN LOTE PADRE
    // Suma todo lo que se ha usado de este padre en cualquier hijo
    @Query("SELECT COALESCE(SUM(tc.quantityUsed), 0) FROM TrackingComposition tc WHERE tc.parent.id = :parentId")
    Double getUsedQuantityByParent(@Param("parentId") Long parentId);

}
package com.mesacer.trackingnes.trackingnes_app.repository.specs;

import org.springframework.data.jpa.domain.Specification;
import com.mesacer.trackingnes.trackingnes_app.model.Tracking;

import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TrackingSpecifications {

    public static Specification<Tracking> withDynamicFilter(Long phaseId, Long productId, Long teamId, Long id, String logisticUnit, LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (phaseId != null) {
                predicates.add(criteriaBuilder.equal(root.get("phase").get("id"), phaseId));
            }
            if (productId != null) {
                predicates.add(criteriaBuilder.equal(root.get("product").get("id"), productId));
            }
            if (teamId != null) {
                predicates.add(criteriaBuilder.equal(root.get("team").get("id"), teamId));
            }
            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }
            if (logisticUnit != null && !logisticUnit.isEmpty()) {
                // Parseamos a Long o String según tu BD. Asumo Long aquí por tu entidad, 
                // pero si el filtro viene como texto parcial, logisticUnit debería ser String en BD.
                predicates.add(criteriaBuilder.equal(root.get("logisticUnit"), Long.valueOf(logisticUnit)));
            }
            if (date != null) {
                // Filtrar por día (ignorando hora)
                LocalDateTime startOfDay = date.atStartOfDay();
                LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
                predicates.add(criteriaBuilder.between(root.get("startTime"), startOfDay, endOfDay));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
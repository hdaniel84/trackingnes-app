package com.mesacer.trackingnes.trackingnes_app.repository.specs;

import com.mesacer.trackingnes.trackingnes_app.model.Product;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {

    public static Specification<Product> hasCodePrefixes(List<String> prefixes) {
        return (root, query, criteriaBuilder) -> {
            // Si no hay prefijos, devolvemos todos (o ninguno, según tu regla)
            if (prefixes == null || prefixes.isEmpty()) {
                return criteriaBuilder.conjunction(); // Devuelve todo
            }

            // Construimos una lista de condiciones OR
            List<Predicate> predicates = new ArrayList<>();
            for (String prefix : prefixes) {
                // LIKE 'FF%'
                predicates.add(criteriaBuilder.like(root.get("productCode"), prefix + "%"));
            }

            // Unimos con OR: (code LIKE 'FF%' OR code LIKE 'FS%' OR ...)
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
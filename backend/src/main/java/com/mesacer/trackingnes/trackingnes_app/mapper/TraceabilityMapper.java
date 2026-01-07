package com.mesacer.trackingnes.trackingnes_app.mapper;

import com.mesacer.trackingnes.trackingnes_app.dto.TraceabilityNodeDTO;
import com.mesacer.trackingnes.trackingnes_app.model.Tracking;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TraceabilityMapper {

    // MAPPING PRINCIPAL
    @Mapping(target = "key", expression = "java(String.valueOf(entity.getId()))")
    @Mapping(target = "expanded", constant = "true")
    @Mapping(target = "type", constant = "person")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "data", source = "entity")
    @Mapping(target = "styleClass", ignore = true)
    TraceabilityNodeDTO toDTO(Tracking entity);

    // MAPPING DE DATOS INTERNOS (NodeData)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "label", source = "phase.description")
    @Mapping(target = "productDesc", source = "product.description")
    @Mapping(target = "productCode", source = "product.productCode")
    @Mapping(target = "team", source = "team.description")
    @Mapping(target = "shift", source = "shift.description")
    @Mapping(target = "quantity", expression = "java(entity.getQuantity() + \" un.\")")

    // --- CAMBIO AQUÍ ---
    // Usamos 'source' apuntando a la lista nueva y un formateador 'qualifiedByName'
    @Mapping(target = "logisticUnit", source = "logisticUnits", qualifiedByName = "formatLogisticUnits")

    @Mapping(target = "date", source = "startTime", qualifiedByName = "formatDate")
    @Mapping(target = "time", source = "startTime", qualifiedByName = "formatTime")
    TraceabilityNodeDTO.NodeData toNodeData(Tracking entity);

    // LOGICA VISUAL (CSS) - Se mantiene igual
    @AfterMapping
    default void determineStyle(Tracking source, @MappingTarget TraceabilityNodeDTO target) {
        String phaseName = source.getPhase().getDescription().toUpperCase();

        if (phaseName.contains("PRENSA")) {
            target.setStyleClass("border-l-4 border-blue-500 bg-blue-50 dark:bg-blue-900/20");
        } else if (phaseName.contains("VIDRAGEM")) {
            target.setStyleClass("border-l-4 border-green-500 bg-green-50 dark:bg-green-900/20");
        } else if (phaseName.contains("FORNO")) {
            target.setStyleClass("border-l-4 border-orange-500 bg-orange-50 dark:bg-orange-900/20");
        } else if (phaseName.contains("ESCOLHA") || phaseName.contains("EMBALAGEM")) {
            target.setStyleClass("border-l-4 border-purple-500 bg-purple-50 dark:bg-purple-900/20");
        } else {
            target.setStyleClass("border-l-4 border-gray-400 bg-white dark:bg-surface-800");
        }
    }

    // HELPERS DE FORMATO

    // --- NUEVO HELPER PARA LISTA DE CARROS ---
    @Named("formatLogisticUnits")
    default String formatLogisticUnits(List<Long> units) {
        if (units == null || units.isEmpty()) {
            return "N/A"; // O "S/ Carro"
        }

        // OPCIÓN A: Mostrar todos separados por coma (ej: "101, 102, 103")
        // return units.toString().replace("[", "").replace("]", "");

        // OPCIÓN B (Recomendada): Mostrar con estilo inteligente si son muchos
        if (units.size() > 3) {
            // Si hay más de 3, mostramos: "101, 102... (+2)"
            return units.get(0) + ", " + units.get(1) + "... (+" + (units.size() - 2) + ")";
        }

        // Si son pocos, los mostramos limpios
        return units.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    @Named("formatDate")
    default String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate().toString() : "";
    }

    @Named("formatTime")
    default String formatTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalTime().toString().substring(0, 5) : "";
    }
}
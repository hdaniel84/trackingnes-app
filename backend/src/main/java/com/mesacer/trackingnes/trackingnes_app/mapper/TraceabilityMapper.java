package com.mesacer.trackingnes.trackingnes_app.mapper;

import com.mesacer.trackingnes.trackingnes_app.dto.TraceabilityNodeDTO;
import com.mesacer.trackingnes.trackingnes_app.model.Tracking;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface TraceabilityMapper {

    // MAPPING PRINCIPAL
    @Mapping(target = "key", expression = "java(String.valueOf(entity.getId()))")
    @Mapping(target = "expanded", constant = "true")
    @Mapping(target = "type", constant = "person")
    @Mapping(target = "children", ignore = true) // La recursión la maneja el Service
    @Mapping(target = "data", source = "entity") // Delegamos el mapeo de datos internos
    @Mapping(target = "styleClass", ignore = true) // Lo calculamos en @AfterMapping
    TraceabilityNodeDTO toDTO(Tracking entity);

    // MAPPING DE DATOS INTERNOS (NodeData)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "label", source = "phase.description")
    @Mapping(target = "productDesc", source = "product.description")
    @Mapping(target = "productCode", source = "product.productCode")
    @Mapping(target = "team", source = "team.description")
    @Mapping(target = "shift", source = "shift.description")
    @Mapping(target = "quantity", expression = "java(entity.getQuantity() + \" un.\")")
    @Mapping(target = "logisticUnit", expression = "java(entity.getLogisticUnit() != null ? String.valueOf(entity.getLogisticUnit()) : \"N/A\")")
    @Mapping(target = "date", source = "startTime", qualifiedByName = "formatDate")
    @Mapping(target = "time", source = "startTime", qualifiedByName = "formatTime")
    TraceabilityNodeDTO.NodeData toNodeData(Tracking entity);

    // LOGICA VISUAL (CSS)
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
    @Named("formatDate")
    default String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate().toString() : "";
    }

    @Named("formatTime")
    default String formatTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalTime().toString().substring(0, 5) : "";
    }
}
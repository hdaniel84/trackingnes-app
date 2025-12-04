package com.mesacer.trackingnes.trackingnes_app.exception;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.Map;

public class DatabaseErrorMapper {

    // Mapa de constraints → mensajes amigables
    private static final Map<String, String> DB_MESSAGES = Map.of(
            "Duplicate", "Já existe um registro com esses dados",
            "other_constraint_name", "Outro erro de integridade"
    );

    public static String getFriendlyMessage(DataIntegrityViolationException ex) {
        if (ex.getMostSpecificCause() != null) {
            String cause = ex.getMostSpecificCause().getMessage();
            for (String key : DB_MESSAGES.keySet()) {
                if (cause.contains(key)) {
                    return DB_MESSAGES.get(key);
                }
            }
        }
        // Mensaje genérico si no hay match
        return "Erro de base de dados";
    }
}

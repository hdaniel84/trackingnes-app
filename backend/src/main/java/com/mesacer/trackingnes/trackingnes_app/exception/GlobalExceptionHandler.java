package com.mesacer.trackingnes.trackingnes_app.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ApiError> buildError(HttpStatus status, String message, String detail) {
        return ResponseEntity.status(status)
                .body(new ApiError(
                        status.value(),
                        message,
                        detail,
                        LocalDateTime.now()
                ));
    }

    // Validación de DTO con @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("[VALIDATION] {}", errors);

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Erro de validação",
                errors
        );
    }

    // Errores de negocio controlados
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex) {
        log.warn("[BUSINESS] {}", ex.getMessage());

        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                null
        );
    }

    // Violaciones de base de datos (constraint, unique, FK)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDb(DataIntegrityViolationException ex) {
        String detail = ex.getMostSpecificCause().getMessage();

        log.error("[DATABASE] Violación de integridad: {}", detail, ex);

        return buildError(
                HttpStatus.CONFLICT,
                "Mensagem da base de dados",
                detail
        );
    }

    // Catch-all: errores no contemplados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {
        log.error("[UNHANDLED] Error inesperado", ex);

        // Evitar filtrar detalles técnicos al cliente
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro inesperado",
                "Contacte o administrador"
        );
    }
}

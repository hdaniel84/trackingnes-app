package com.mesacer.trackingnes.trackingnes_app.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

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
                        LocalDateTime.now()));
    }

    // Manejo específico: Validación de DTO con @Valid
    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    public ResponseEntity<ApiError> handleValidation(Exception ex) {
        String errors = "";
        if (ex instanceof MethodArgumentNotValidException manv) {
            errors = manv.getBindingResult().getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } else if (ex instanceof BindException bex) {
            errors = bex.getBindingResult().getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        }

        log.warn("[VALIDATION] {}", errors);
        return buildError(HttpStatus.BAD_REQUEST, "Erro de validação", errors);
    }

    // Manejo específico: errores de negocio controlados
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex) {
        log.warn("[BUSINESS] {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // Manejo específico: Violaciones de base de datos (constraint, unique, FK)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDb(DataIntegrityViolationException ex) {
        // String detail = ex.getMostSpecificCause().getMessage();
        String detail = DatabaseErrorMapper.getFriendlyMessage(ex);
        log.error("[DATABASE] Violación de integridad: {}", detail, ex);
        return buildError(HttpStatus.CONFLICT, "Erro na base de dados", detail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex) {

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife) {
            String fieldName = ife.getPath().get(0).getFieldName();
            String message = "Campo '" + fieldName + "' deve conter apenas números.";

            log.warn("[FORMAT] {}", message);

            return buildError(
                    HttpStatus.BAD_REQUEST,
                    "Formato inválido",
                    message);
        }

        log.warn("[FORMAT] Corpo de requisição inválido.");
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Formato inválido no corpo da requisição",
                ex.getMostSpecificCause().getMessage());
    }

    // Manejo automático y centralizado de excepciones no contempladas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {
        HttpStatus status = mapExceptionToStatus(ex);
        String message = mapExceptionToMessage(ex);

        log.error("[{}] {}", status.name(), ex.getMessage(), ex);

        return buildError(status, message, status.is5xxServerError() ? "Contacte o administrador" : ex.getMessage());
    }

    // Mapeo automático de excepciones a códigos HTTP
    private HttpStatus mapExceptionToStatus(Exception ex) {
        return switch (ex) {
            case HttpRequestMethodNotSupportedException ignored -> HttpStatus.METHOD_NOT_ALLOWED;
            case IllegalArgumentException ignored -> HttpStatus.BAD_REQUEST;
            case IllegalStateException ignored -> HttpStatus.BAD_REQUEST;
            // Puedes agregar más mapeos aquí según tus necesidades
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    // Mensajes amigables automáticos según la excepción
    private String mapExceptionToMessage(Exception ex) {
        return switch (ex) {
            case HttpRequestMethodNotSupportedException ignored -> "Método HTTP no soportado";
            case IllegalArgumentException ignored -> "Argumento inválido";
            case IllegalStateException ignored -> "Estado inválido para esta operación";
            default -> "Erro inesperado";
        };
    }
}

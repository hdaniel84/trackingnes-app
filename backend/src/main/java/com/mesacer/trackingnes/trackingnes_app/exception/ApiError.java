package com.mesacer.trackingnes.trackingnes_app.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiError {
    private int status;
    private String message;
    private String detail;
    private LocalDateTime timestamp;
}

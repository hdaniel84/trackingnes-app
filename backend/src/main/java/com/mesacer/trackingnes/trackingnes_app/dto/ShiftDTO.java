package com.mesacer.trackingnes.trackingnes_app.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class ShiftDTO {
    private Long id;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
}

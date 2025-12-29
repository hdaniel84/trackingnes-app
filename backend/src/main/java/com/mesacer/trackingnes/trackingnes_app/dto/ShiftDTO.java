package com.mesacer.trackingnes.trackingnes_app.dto;

import java.time.LocalTime;

import lombok.Data;

@Data
public class ShiftDTO {
    private Long id;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
}

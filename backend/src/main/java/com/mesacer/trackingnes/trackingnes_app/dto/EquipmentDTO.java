package com.mesacer.trackingnes.trackingnes_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EquipmentDTO {

    private Long id;

    @NotBlank
    private String description;

    @NotNull
    private Long sectionId;

    private String sectionDescription;
}

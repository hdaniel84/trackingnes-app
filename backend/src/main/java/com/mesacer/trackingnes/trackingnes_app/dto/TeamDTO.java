package com.mesacer.trackingnes.trackingnes_app.dto;

import lombok.Data;

@Data
public class TeamDTO {
    private Long id;
    private String description;
    private Long sectionId;
    private String sectionDescription;
    private Long responsableId;
}

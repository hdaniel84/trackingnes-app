package com.mesacer.trackingnes.trackingnes_app.dto;

import lombok.Data;

@Data
public class ParameterFornoEntradaDTO {

    private Long trackingId;
    private Long parameterId;
    private String parameterDescription;
    private String value;
}

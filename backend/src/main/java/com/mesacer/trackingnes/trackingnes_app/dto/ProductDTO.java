package com.mesacer.trackingnes.trackingnes_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDTO {

    private Long id;

    @NotBlank
    @NotNull(message = "Campo obrigatório")
    private String codigoProduto;

    @NotBlank
    @NotNull(message = "Campo obrigatório")
    private String description;
}

package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product")
    private Long id;

    @Column(name = "product_code", nullable = false, length = 50)
    private String codigoProduto;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @OneToOne
    @JoinColumn(name = "id_shape", nullable = false)
    private ProductShape shape;

    @OneToOne
    @JoinColumn(name = "id_glass", nullable = false)
    private ProductGlass glass;

    @OneToOne
    @JoinColumn(name = "id_decoration", nullable = false)
    private ProductDecoration decoration;

}
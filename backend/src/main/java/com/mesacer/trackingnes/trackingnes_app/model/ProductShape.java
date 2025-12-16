package com.mesacer.trackingnes.trackingnes_app.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "product_shapes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductShape {
    
    @Id
    private String id;
    
    @Column(name = "description", nullable = false, length = 100)
    private String description;
}
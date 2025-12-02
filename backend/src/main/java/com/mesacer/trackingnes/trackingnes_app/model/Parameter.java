package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false, length = 100)
    private String description;
}

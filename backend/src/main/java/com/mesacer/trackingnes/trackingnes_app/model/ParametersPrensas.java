package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parameters_prensas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParametersPrensas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* 
    // Relación con TrackingPrensas
    @ManyToOne
    @JoinColumn(name = "id_tracking", nullable = false)
    private TrackingPrensas tracking;
*/
    // Relación con Parameter
    @ManyToOne
    @JoinColumn(name = "id_parameter", nullable = false)
    private Parameter parameter;

    @Column(name = "value", nullable = false, length = 100)
    private String value;
}

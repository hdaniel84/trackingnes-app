package com.mesacer.trackingnes.trackingnes_app.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parameters_vidragem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterVidragem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tracking", nullable = false)
    private TrackingVidragem trackingVidragem;

    // Relación con Parameter
    @ManyToOne
    @JoinColumn(name = "id_parameter", nullable = false)
    private Parameter parameter;

    @Column(name = "value", nullable = false, length = 100)
    private String value;
}

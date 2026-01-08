package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;

@Entity
@Immutable // 1. Le dice a Hibernate que esto es solo lectura
@Table(name = "v_tracking_availability") // 2. Apunta a la vista
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingAvailability implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // El mismo ID que el Tracking

    @Column(name = "initial_quantity")
    private Integer initialQuantity;

    @Column(name = "used_quantity")
    private Integer usedQuantity;

    @Column(name = "remaining_quantity")
    private Integer remainingQuantity;

    // 3. Relación Inversa (Opcional pero útil): Unir con el Tracking principal
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    @ToString.Exclude
    private Tracking tracking;
}
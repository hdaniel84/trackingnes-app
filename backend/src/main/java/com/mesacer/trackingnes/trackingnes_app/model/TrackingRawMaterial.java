package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tracking_raw_materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingRawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tracking", nullable = false)
    private Tracking tracking;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_raw_material_type", nullable = false)
    private RawMaterial rawMaterialType;

    @Column(nullable = false, length = 100)
    private String value;
}

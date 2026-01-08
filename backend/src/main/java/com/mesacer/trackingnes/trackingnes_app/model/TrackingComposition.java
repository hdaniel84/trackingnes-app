package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tracking_composition")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingComposition {

    @EmbeddedId
    private TrackingCompositionId id = new TrackingCompositionId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("childId")
    @JoinColumn(name = "id_child")
    @ToString.Exclude
    private Tracking child;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("parentId")
    @JoinColumn(name = "id_parent")
    @ToString.Exclude
    private Tracking parent;

    @Column(name = "quantity_used", nullable = false)
    private Integer quantityUsed;
}
package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tracking_vidragem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TrackingVidragem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_team", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "id_shift", nullable = false)
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id_equipment", nullable = false)
    private Equipment equipment;

    @Column(name = "id_logistic_unit_in", nullable = false)
    private Integer logisticUnitInId;

    @Column(name = "id_logistic_unit_out", nullable = false)
    private Integer logisticUnitOutId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "id_raw_material_type", nullable = false)
    private RawMaterial rawMaterial;

    @Column(name = "lot_raw_material", nullable = false, length = 50)
    private String lote;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @OneToMany(mappedBy = "trackingVidragem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParameterVidragem> parameters = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;
}

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
@Table(name = "tracking_prensas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TrackingPrensas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @ManyToOne
    @JoinColumn(name = "id_team", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "id_shift", nullable = false)
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "id_raw_material_type", nullable = false)
    private RawMaterial rawMaterial;

    @Column(name = "lote_raw_material")
    private String lote;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "id_logistic_unit")
    private Integer logisticUnit;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @ManyToOne
    @JoinColumn(name = "id_equipment", nullable = false)
    private Equipment equipment;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "trackingPrensas", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametersPrensas> parameters = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdTime;

    @CreatedBy
    @Column(name = "created_by")
    private Long createdUserId;

}

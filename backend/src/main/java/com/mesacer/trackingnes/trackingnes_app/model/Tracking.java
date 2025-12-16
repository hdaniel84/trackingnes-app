package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tracking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Tracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_team")
    private Team team;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_shift")
    private Shift shift;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_product")
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_equipment")
    private Equipment equipment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_phase")
    private Phase phase;

    @Column(name = "id_logistic_unit")
    private Long logisticUnit;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "id_tracking_source")
    private Tracking trackingSource;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @OneToMany(mappedBy = "tracking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrackingRawMaterial> rawMaterials = new ArrayList<>();

    @OneToMany(mappedBy = "tracking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrackingParameter> parameters = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;
}

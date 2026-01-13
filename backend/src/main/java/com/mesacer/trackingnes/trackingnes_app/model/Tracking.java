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

    // 2. NUEVO: LISTA DE EQUIPAMIENTOS
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tracking_equipments", joinColumns = @JoinColumn(name = "id_tracking"), inverseJoinColumns = @JoinColumn(name = "id_equipment"))
    private List<Equipment> auxiliaryEquipments = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_phase")
    private Phase phase;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tracking_logistic_units", joinColumns = @JoinColumn(name = "tracking_id"))
    @Column(name = "logistic_unit") // Nombre de la columna del valor
    private List<Long> logisticUnits = new ArrayList<>();

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "quantity_scrap")
    private Integer quantityScrap = 0;

    @Column(name = "scrap_reason")
    private String scrapReason;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<TrackingComposition> sourceComposition = new ArrayList<>();

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<TrackingComposition> destinationComposition = new ArrayList<>();

    @OneToOne(mappedBy = "tracking", fetch = FetchType.LAZY)
    private TrackingAvailability availability;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @OneToMany(mappedBy = "tracking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TrackingRawMaterial> rawMaterials = new ArrayList<>();

    @OneToMany(mappedBy = "tracking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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

    // Helper para agregar orígenes fácilmente
    public void addSource(Tracking parent, Integer qtyUsed) {
        TrackingComposition link = new TrackingComposition();
        link.setChild(this);
        link.setParent(parent);
        link.setQuantityUsed(qtyUsed);
        this.sourceComposition.add(link);
    }
}

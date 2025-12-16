package com.mesacer.trackingnes.trackingnes_app.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tracking_parameters",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_tracking","id_parameter"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tracking")
    private Tracking tracking;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_parameter")
    private Parameter parameter;

    @Column(name = "value_number")
    private Double valueNumber;

    @Column(name = "value_string", columnDefinition = "TEXT")
    private String valueString;

    @Column(name = "value_bool")
    private Boolean valueBool;

    @Column(name = "value_date")
    private LocalDateTime valueDate;
}

package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "raw_material_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sap_code", nullable = false, length = 50)
    private String sapCode;

    @Column(name = "description", nullable = false, length = 50)
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_section")
    private Section section;
}

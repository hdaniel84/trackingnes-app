package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_team")
    private Long id;

    @Column(name = "description", length = 100)
    private String description;

    // Relación con Section (puede ser nullable)
    @ManyToOne
    @JoinColumn(name = "id_section")
    private Section section;

    // Relación con Person (responsable)
    @ManyToOne
    @JoinColumn(name = "id_responsable")
    private Person responsable;
}

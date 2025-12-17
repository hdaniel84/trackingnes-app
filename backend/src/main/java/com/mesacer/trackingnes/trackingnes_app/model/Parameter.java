package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_phase")
    private Phase phase;

    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", columnDefinition = "ENUM('NUMBER','STRING','BOOL','DATE')", nullable = false)
    private ValueType valueType;

    @Column(name = "mandatory")
    private Boolean mandatory;

    @Column(name = "code")
    private String code;

}

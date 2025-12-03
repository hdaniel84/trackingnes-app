package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_person")
    private Long id;

    @Column(name = "employee_number", length = 10)
    private String employeeNumber;

    @Column(name = "name", length = 45)
    private String name;

    @Column(name = "lastname", length = 45)
    private String lastname;
}

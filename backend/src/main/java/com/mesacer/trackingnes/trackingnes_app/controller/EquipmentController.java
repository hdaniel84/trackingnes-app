package com.mesacer.trackingnes.trackingnes_app.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mesacer.trackingnes.trackingnes_app.dto.EquipmentDTO;
import com.mesacer.trackingnes.trackingnes_app.service.EquipmentService;

@RestController
@RequestMapping("/api/equipments")
public class EquipmentController {

    private final EquipmentService service;

    public EquipmentController(EquipmentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }
}

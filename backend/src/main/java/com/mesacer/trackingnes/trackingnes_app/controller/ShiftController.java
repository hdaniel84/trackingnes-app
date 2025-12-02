package com.mesacer.trackingnes.trackingnes_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mesacer.trackingnes.trackingnes_app.dto.ShiftDTO;
import com.mesacer.trackingnes.trackingnes_app.service.ShiftService;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    private final ShiftService service;

    public ShiftController(ShiftService service) {
        this.service = service;
    }

    @GetMapping
    public List<ShiftDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftDTO> getById(@PathVariable Long id) {
        ShiftDTO dto = service.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }
}

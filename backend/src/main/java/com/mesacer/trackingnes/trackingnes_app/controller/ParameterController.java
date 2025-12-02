package com.mesacer.trackingnes.trackingnes_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mesacer.trackingnes.trackingnes_app.dto.ParameterDTO;
import com.mesacer.trackingnes.trackingnes_app.service.ParameterService;

@RestController
@RequestMapping("/api/parameters")
public class ParameterController {

    private final ParameterService service;

    public ParameterController(ParameterService service) {
        this.service = service;
    }

    @GetMapping
    public List<ParameterDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParameterDTO> getById(@PathVariable Long id) {
        ParameterDTO dto = service.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }
}

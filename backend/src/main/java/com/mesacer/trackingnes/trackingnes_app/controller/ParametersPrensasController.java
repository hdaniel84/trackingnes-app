package com.mesacer.trackingnes.trackingnes_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mesacer.trackingnes.trackingnes_app.dto.ParametersPrensasDTO;
import com.mesacer.trackingnes.trackingnes_app.service.ParametersPrensasService;

@RestController
@RequestMapping("/api/parameters-prensas")
public class ParametersPrensasController {

    private final ParametersPrensasService service;

    public ParametersPrensasController(ParametersPrensasService service) {
        this.service = service;
    }

    @GetMapping
    public List<ParametersPrensasDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParametersPrensasDTO> getById(@PathVariable Long id) {
        ParametersPrensasDTO dto = service.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }
}

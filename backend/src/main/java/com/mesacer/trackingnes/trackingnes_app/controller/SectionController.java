package com.mesacer.trackingnes.trackingnes_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mesacer.trackingnes.trackingnes_app.dto.SectionDTO;
import com.mesacer.trackingnes.trackingnes_app.service.SectionService;

@RestController
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService service;

    public SectionController(SectionService service) {
        this.service = service;
    }

    @GetMapping
    public List<SectionDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionDTO> getById(@PathVariable Long id) {
        SectionDTO dto = service.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }
}

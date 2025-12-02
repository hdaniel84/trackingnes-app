package com.mesacer.trackingnes.trackingnes_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mesacer.trackingnes.trackingnes_app.dto.RawMaterialDTO;
import com.mesacer.trackingnes.trackingnes_app.service.RawMaterialService;

@RestController
@RequestMapping("/api/raw-materials")
public class RawMaterialController {

    private final RawMaterialService service;

    public RawMaterialController(RawMaterialService service) {
        this.service = service;
    }

    @GetMapping
    public List<RawMaterialDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialDTO> getById(@PathVariable Long id) {
        RawMaterialDTO dto = service.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public RawMaterialDTO create(@RequestBody RawMaterialDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialDTO> update(@PathVariable Long id, @RequestBody RawMaterialDTO dto) {
        dto.setId(id);
        RawMaterialDTO updated = service.save(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

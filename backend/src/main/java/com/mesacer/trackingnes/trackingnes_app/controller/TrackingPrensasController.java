package com.mesacer.trackingnes.trackingnes_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingPrensasDTO;
import com.mesacer.trackingnes.trackingnes_app.service.TrackingPrensasService;

@RestController
@RequestMapping("/api/tracking-prensas")
public class TrackingPrensasController {

    private final TrackingPrensasService service;

    public TrackingPrensasController(TrackingPrensasService service) {
        this.service = service;
    }

    @GetMapping
    public List<TrackingPrensasDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackingPrensasDTO> getById(@PathVariable Long id) {
        TrackingPrensasDTO dto = service.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public TrackingPrensasDTO create(@RequestBody TrackingPrensasDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackingPrensasDTO> update(@PathVariable Long id, @RequestBody TrackingPrensasDTO dto) {
        dto.setId(id);
        TrackingPrensasDTO updated = service.save(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

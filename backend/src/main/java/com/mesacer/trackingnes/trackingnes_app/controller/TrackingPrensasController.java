package com.mesacer.trackingnes.trackingnes_app.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingPrensasDTO;
import com.mesacer.trackingnes.trackingnes_app.service.TrackingPrensasService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tracking-prensas")
public class TrackingPrensasController {

    private final TrackingPrensasService service;

    public TrackingPrensasController(TrackingPrensasService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TrackingPrensasDTO>> getAll(
            @PageableDefault(sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TrackingPrensasDTO> page = service.findAll(pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(page.getTotalElements()))
                .header("X-Total-Pages", String.valueOf(page.getTotalPages()))
                .body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackingPrensasDTO> getById(@PathVariable Long id) {
        TrackingPrensasDTO dto = service.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public TrackingPrensasDTO create(@Valid @RequestBody TrackingPrensasDTO dto) {
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

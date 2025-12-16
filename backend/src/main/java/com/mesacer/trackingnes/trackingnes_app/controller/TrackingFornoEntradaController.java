package com.mesacer.trackingnes.trackingnes_app.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingFornoEntradaDTO;
import com.mesacer.trackingnes.trackingnes_app.service.TrackingFornoEntradaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tracking-forno-entrada")
public class TrackingFornoEntradaController {

    private final TrackingFornoEntradaService service;

    public TrackingFornoEntradaController(TrackingFornoEntradaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TrackingFornoEntradaDTO>> getAll(
            @PageableDefault(sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TrackingFornoEntradaDTO> page = service.findAll(pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(page.getTotalElements()))
                .header("X-Total-Pages", String.valueOf(page.getTotalPages()))
                .body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackingFornoEntradaDTO> getById(@PathVariable Long id) {
        TrackingFornoEntradaDTO dto = service.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public TrackingFornoEntradaDTO create(@Valid @RequestBody TrackingFornoEntradaDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackingFornoEntradaDTO> update(@PathVariable Long id,
            @RequestBody TrackingFornoEntradaDTO dto) {
        dto.setId(id);
        TrackingFornoEntradaDTO updated = service.save(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

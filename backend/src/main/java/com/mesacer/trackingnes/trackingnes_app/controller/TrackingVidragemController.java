package com.mesacer.trackingnes.trackingnes_app.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingVidragemDTO;
import com.mesacer.trackingnes.trackingnes_app.service.TrackingVidragemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tracking-vidragem")
public class TrackingVidragemController {

    private final TrackingVidragemService service;

    public TrackingVidragemController(TrackingVidragemService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TrackingVidragemDTO>> getAll(
            @PageableDefault(sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TrackingVidragemDTO> page = service.findAll(pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(page.getTotalElements()))
                .header("X-Total-Pages", String.valueOf(page.getTotalPages()))
                .body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackingVidragemDTO> getById(@PathVariable Long id) {
        TrackingVidragemDTO dto = service.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public TrackingVidragemDTO create(@Valid @RequestBody TrackingVidragemDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackingVidragemDTO> update(@PathVariable Long id, @RequestBody TrackingVidragemDTO dto) {
        dto.setId(id);
        TrackingVidragemDTO updated = service.save(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.mesacer.trackingnes.trackingnes_app.controller;

import com.mesacer.trackingnes.trackingnes_app.dto.TraceabilityNodeDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.service.TraceabilityService;
import com.mesacer.trackingnes.trackingnes_app.service.TrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor // Genera el constructor automáticamente para los campos final
public class TrackingController {

    private final TrackingService service;
    private final TraceabilityService traceabilityService;

    @GetMapping
    public ResponseEntity<List<TrackingResponseDTO>> getAll(
            @RequestParam(required = false) Long phaseId,
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
        // Pasamos el phaseId al servicio
        Page<TrackingResponseDTO> page = service.getAll(phaseId, pageable);

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-total-count", String.valueOf(page.getTotalElements()));
        headers.add("x-total-pages", String.valueOf(page.getTotalPages()));

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackingResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<TrackingResponseDTO>> getCandidates(
            @RequestParam List<Long> phaseIds,
            @RequestParam(required = false) String referenceId, // Recibimos como String
            @RequestParam(defaultValue = "SHAPE") String filterType) {

        return ResponseEntity.ok(service.findCandidates(phaseIds, referenceId, filterType));
    }

    /**
     * Endpoint para obtener el árbol genealógico.
     * GET /api/tracking/{id}/traceability
     */
    @GetMapping("/{id}/traceability")
    public ResponseEntity<TraceabilityNodeDTO> getTraceabilityTree(@PathVariable Long id) {
        TraceabilityNodeDTO tree = traceabilityService.getAncestryTree(id);

        if (tree == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(tree);
    }

    @PostMapping
    public ResponseEntity<TrackingResponseDTO> create(@Valid @RequestBody TrackingRequestDTO request) {
        TrackingResponseDTO created = service.create(request);
        // Devolvemos 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackingResponseDTO> update(@PathVariable Long id,
            @Valid @RequestBody TrackingRequestDTO request) {
        // Pasamos el ID y el RequestDTO por separado al servicio
        TrackingResponseDTO updated = service.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

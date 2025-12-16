package com.mesacer.trackingnes.trackingnes_app.controller;

import com.mesacer.trackingnes.trackingnes_app.dto.TrackingRequestDTO;
import com.mesacer.trackingnes.trackingnes_app.dto.TrackingResponseDTO;
import com.mesacer.trackingnes.trackingnes_app.service.TrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor // Genera el constructor automáticamente para los campos final
public class TrackingController {

    private final TrackingService service;

    @GetMapping
    public ResponseEntity<List<TrackingResponseDTO>> getAll(
            @PageableDefault(sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {

        // Ahora el servicio devuelve Page<TrackingResponseDTO>
        Page<TrackingResponseDTO> page = service.findAll(pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(page.getTotalElements()))
                .header("X-Total-Pages", String.valueOf(page.getTotalPages()))
                .body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackingResponseDTO> getById(@PathVariable Long id) {
        // Ya no comprobamos null aquí, porque el servicio lanza EntityNotFoundException
        // lo cual es manejado globalmente (ver abajo) o devuelve el objeto si existe.
        return ResponseEntity.ok(service.findById(id));
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

/*
 * package com.mesacer.trackingnes.trackingnes_app.controller;
 * 
 * import java.util.List;
 * 
 * import org.springframework.data.domain.Page;
 * import org.springframework.data.domain.Pageable;
 * import org.springframework.data.domain.Sort;
 * import org.springframework.data.web.PageableDefault;
 * import org.springframework.http.ResponseEntity;
 * import org.springframework.web.bind.annotation.*;
 * 
 * import com.mesacer.trackingnes.trackingnes_app.dto.TrackingDTO;
 * import com.mesacer.trackingnes.trackingnes_app.service.TrackingService;
 * 
 * import jakarta.validation.Valid;
 * 
 * @RestController
 * 
 * @RequestMapping("/api/tracking")
 * public class TrackingController {
 * 
 * private final TrackingService service;
 * 
 * public TrackingController(TrackingService service) {
 * this.service = service;
 * }
 * 
 * @GetMapping
 * public ResponseEntity<List<TrackingDTO>> getAll(
 * 
 * @PageableDefault(sort = "startTime", direction = Sort.Direction.DESC)
 * Pageable pageable) {
 * Page<TrackingDTO> page = service.findAll(pageable);
 * 
 * return ResponseEntity.ok()
 * .header("X-Total-Count", String.valueOf(page.getTotalElements()))
 * .header("X-Total-Pages", String.valueOf(page.getTotalPages()))
 * .body(page.getContent());
 * }
 * 
 * @GetMapping("/{id}")
 * public ResponseEntity<TrackingDTO> getById(@PathVariable Long id) {
 * TrackingDTO dto = service.findById(id);
 * return dto != null ? ResponseEntity.ok(dto) :
 * ResponseEntity.notFound().build();
 * }
 * 
 * @PostMapping
 * public TrackingDTO create(@Valid @RequestBody TrackingDTO dto) {
 * return service.save(dto);
 * }
 * 
 * @PutMapping("/{id}")
 * public ResponseEntity<TrackingDTO> update(@PathVariable Long id, @RequestBody
 * TrackingDTO dto) {
 * dto.setId(id);
 * TrackingDTO updated = service.save(dto);
 * return ResponseEntity.ok(updated);
 * }
 * 
 * @DeleteMapping("/{id}")
 * public ResponseEntity<Void> delete(@PathVariable Long id) {
 * service.delete(id);
 * return ResponseEntity.noContent().build();
 * }
 * }
 */
package com.mesacer.trackingnes.trackingnes_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mesacer.trackingnes.trackingnes_app.dto.TeamDTO;
import com.mesacer.trackingnes.trackingnes_app.service.TeamService;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService service;

    public TeamController(TeamService service) {
        this.service = service;
    }

    @GetMapping
    public List<TeamDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getById(@PathVariable Long id) {
        TeamDTO dto = service.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

}

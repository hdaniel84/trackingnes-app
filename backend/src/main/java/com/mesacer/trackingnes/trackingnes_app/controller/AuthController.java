package com.mesacer.trackingnes.trackingnes_app.controller;

import com.mesacer.trackingnes.trackingnes_app.dto.LoginRequest;
import com.mesacer.trackingnes.trackingnes_app.service.TokenService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Ruta permitida por WebSecurityConfig
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    // Spring inyecta las dependencias necesarias
    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        
        // 1. Intentar Autenticar al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. Si la autenticación es exitosa, guardar en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generar el Token JWT
        String jwt = tokenService.generateToken(authentication);

        // 4. Devolver el token al frontend
        return ResponseEntity.ok(new JwtResponse(jwt));
    }
    
    // Clase simple para encapsular la respuesta del JWT
    private record JwtResponse(String token) {}
}
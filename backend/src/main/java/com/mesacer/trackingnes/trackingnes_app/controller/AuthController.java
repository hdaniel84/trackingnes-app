package com.mesacer.trackingnes.trackingnes_app.controller;

import com.mesacer.trackingnes.trackingnes_app.dto.LoginRequest;
import com.mesacer.trackingnes.trackingnes_app.service.TokenService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // 1. Autenticar
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. Establecer contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generar Token
        String jwt = tokenService.generateToken(authentication);

        // 4. Obtener detalles del usuario (UserDetails)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 5. Extraer Roles y Privilegios
        // En Spring Security, todo son "Authorities". 
        // Convención: Los ROLES suelen empezar con "ROLE_", el resto son privilegios.
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_")) // Filtramos Roles
                .collect(Collectors.toList());

        List<String> privileges = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> !auth.startsWith("ROLE_")) // El resto son Privilegios
                .collect(Collectors.toList());

        // 6. Devolver respuesta completa
        return ResponseEntity.ok(new JwtResponse(
                jwt, 
                userDetails.getUsername(), 
                roles, 
                privileges
        ));
    }

    // Actualizamos el Record para incluir lo que pide Pinia Store
    public record JwtResponse(
            String token, 
            String username, 
            List<String> roles, 
            List<String> privileges
    ) {}
}
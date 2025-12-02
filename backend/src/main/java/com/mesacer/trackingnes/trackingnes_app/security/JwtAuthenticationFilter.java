package com.mesacer.trackingnes.trackingnes_app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mesacer.trackingnes.trackingnes_app.service.TokenService;
import com.mesacer.trackingnes.trackingnes_app.service.UserDetailsServiceImpl;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(TokenService tokenService, UserDetailsServiceImpl userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Obtener el Token de la solicitud
            String jwt = getJwtFromRequest(request);

            // 2. Validar y Procesar si existe
            if (StringUtils.hasText(jwt) && tokenService.validateToken(jwt)) {
                
                // Extraer el username del token
                String username = tokenService.getUsernameFromToken(jwt);

                // Cargar los detalles del usuario
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Crear el objeto de autenticación
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Cargar el usuario en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Manejar errores de token (token expirado, inválido, etc.)
            logger.error("No se pudo establecer la autenticación del usuario.", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Verificar si la cabecera Authorization contiene el prefijo "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Retorna solo el token (después de "Bearer ")
        }
        return null;
    }
}
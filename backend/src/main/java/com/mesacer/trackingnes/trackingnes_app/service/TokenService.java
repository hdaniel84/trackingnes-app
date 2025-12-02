package com.mesacer.trackingnes.trackingnes_app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import com.mesacer.trackingnes.trackingnes_app.config.JwtProperties;

@Service
public class TokenService {

    // Almacenaremos la clave una vez generada
    private final Key key;

    private static final long EXPIRATION_TIME = 86400000;

    // Constructor para inicializar la clave
    public TokenService(JwtProperties jwtProperties) {
        String jwtSecret = jwtProperties.getSecret();
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();
    }


    public boolean validateToken(String authToken) {
        try {
            // Parsear el token usando la clave secreta
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception ex) {
            // Aquí puedes loggear el error específico (ExpiredJwtException,
            // MalformedJwtException, etc.)
            // logger.error("JWT inválido o expirado", ex);
            return false;
        }
    }
    

    public String getUsernameFromToken(String token) {
        // Obtener los claims (cuerpo) del token
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // El 'subject' es donde guardamos el nombre de usuario en el método
        // generateToken()
        return claims.getSubject();
    }
}
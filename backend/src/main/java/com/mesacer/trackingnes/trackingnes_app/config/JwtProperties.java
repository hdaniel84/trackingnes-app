package com.mesacer.trackingnes.trackingnes_app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "jwt") 
@Data // Lombok para getters/setters
public class JwtProperties {
    /**
     * La llave secreta usada para firmar los JSON Web Tokens.
     * Debe ser una cadena larga y segura.
     */
    private String secret;
}
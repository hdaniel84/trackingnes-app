package com.mesacer.trackingnes.trackingnes_app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Arrays;

@Configuration
@EnableWebSecurity 
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/tracking-prensas/**").hasAnyAuthority("READ_PRENSAS")
                        .requestMatchers(HttpMethod.GET, "/api/tracking-vidragem/**").hasAnyAuthority("READ_VIDRAGEM")
                        .requestMatchers(HttpMethod.POST,"/api/tracking-prensas/**").hasAnyAuthority("WRITE_PRENSAS")
                        .requestMatchers(HttpMethod.POST, "/api/tracking-vidragem/**").hasAnyAuthority("WRITE_VIDRAGEM")
                        .anyRequest().authenticated());

        return http.build();
    }

    // 2. Codificador de Contraseña (PasswordEncoder)
    // Spring Security requiere este Bean para codificar (hashear) las contraseñas
    // de los usuarios.
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt es el algoritmo recomendado y estándar
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 3. Configuración CORS (Permite que Vue.js se comunique)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Define los orígenes permitidos. Cambiar '*' a la URL del frontend de Vue.js
        // en producción (ej: http://localhost:5173)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Permite los métodos HTTP comunes para APIs REST
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Define qué cabeceras (headers) están permitidas (Authorization es clave para
        // JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));

        // Permite que las cookies/credenciales sean enviadas (aunque JWT es stateless,
        // es buena práctica)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración a todos los endpoints (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
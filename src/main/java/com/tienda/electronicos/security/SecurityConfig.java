package com.tienda.electronicos.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.electronicos.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // =========================
    // DEPENDENCIAS
    // =========================
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    // =========================
    // CONSTRUCTOR
    // =========================
    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            UserDetailsServiceImpl userDetailsService
    )
    // throws Exception
    {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    // =========================
    // SECURITY FILTER CHAIN
    // =========================
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Respuestas JSON uniformes para 401 (sin token) y 403 (sin permisos)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                escribirError(objectMapper, request, response, HttpStatus.UNAUTHORIZED,
                                        "Debes iniciar sesión para acceder a este recurso"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                escribirError(objectMapper, request, response, HttpStatus.FORBIDDEN,
                                        "No tienes permisos para acceder a este recurso"))
                )
                // FILTRO JWT
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth

                        // 🔓 AUTH
                        .requestMatchers("/api/auth/**").permitAll()

                        // Allow payments endpoints for mock testing
                        .requestMatchers(HttpMethod.POST, "/api/payments/pse").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/payments/pse/mock/checkout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/payments/status/**").permitAll()

                        // 🔒 ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // -------------------------------------------------
                        // PERMITIR VISUALIZACIÓN PÚBLICA (GET)
                        // -------------------------------------------------
                        .requestMatchers(HttpMethod.GET,
                                "/api/productos",
                                "/api/productos/**",
                                "/api/categorias",
                                "/api/categorias/**"
                        ).permitAll()

                        // 🛒 PRODUCTOS - operaciones que requieren ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")

                        // 📂 CATEGORÍAS - operaciones que requieren ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("ADMIN")

                        // 🛒 CARRITO
                        .requestMatchers(HttpMethod.GET, "/api/carrito/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/carrito/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/carrito/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/carrito/**").hasAnyRole("ADMIN", "USER")

                        // 👤 USUARIOS - la autorización fina (propio usuario o ADMIN) se decide en el controlador
                        .requestMatchers("/api/usuarios/**").authenticated()

                        // 🔐 RESTO
                        .anyRequest().authenticated()
                );

        // FINALIZA CONFIGURACIÓN
        return http.build();
    }

    // =========================
    // AUTHENTICATION MANAGER & PROVIDER
    // =========================
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // =========================
    // AUTHENTICATION PROVIDER
    // =========================
    @Bean
    public AuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder
    )
    // throws Exception
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // =========================
    // CORS (orígenes configurables por app.cors.allowed-origins)
    // =========================
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:4200}") String allowedOrigins
    ) {
        List<String> origenes = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(o -> !o.isEmpty())
                .toList();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(origenes);
        config.addAllowedHeader("*");
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Escribe un ApiError en JSON como respuesta de seguridad (401 / 403)
    private void escribirError(
            ObjectMapper objectMapper,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status,
            String mensaje
    ) throws IOException {
        ApiError error = ApiError.builder()
                .status(status.value())
                .error(status.name())
                .message(mensaje)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), error);
    }

}

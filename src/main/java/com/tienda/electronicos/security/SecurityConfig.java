package com.tienda.electronicos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
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
                        // Permitir PUT del carrito para todos (autenticados y no autenticados)
                        .requestMatchers(HttpMethod.PUT, "/api/carrito/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/carrito/**").hasAnyRole("ADMIN", "USER")

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

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}

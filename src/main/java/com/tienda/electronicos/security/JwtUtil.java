
package com.tienda.electronicos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JWT HS256 (secreto >= 32 chars). Claim "roles" sin prefijo.
 */
@Component
public class JwtUtil {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    // =====================================================
    // DEPENDENCIAS
    // =====================================================
    private final Key key;
    private final long expirationMs;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public JwtUtil(
            @Value("${security.jwt.secret:change_me_very_long_secret_key_2025_32_bytes_min}") String secret,
            @Value("${security.jwt.expiration-ms:86400000}") long expirationMs
    ) {
        // Validar longitud del secreto
        if (secret == null || secret.trim().length() < 32) {
            throw new IllegalArgumentException(
                    "JWT secret demasiado corto. Configura 'security.jwt.secret' con >= 32 caracteres."
            );
        }
        // Inicializar key y expiration
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;

        // Log de inicialización
        log.info("JwtUtil inicializado. Expiration={} ms, SecretLength={}", this.expirationMs, secret.length());
    }

    /** Genera token con subject=correo y claim "roles" (sin prefijo). */
    public String generarToken(String correo, Collection<String> rolesSinPrefijo) {

        // Claims y roles
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = rolesSinPrefijo == null
                ? Collections.emptyList()
                : rolesSinPrefijo.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        claims.put("roles", roles);

        // Fechas emisión y expiración
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        // Generar token
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(correo)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Validez estructural, firma y expiración. */
    public boolean esTokenValido(String token) {
        // Verificar token
        try {
            getClaims(token);
            return true;
        }
        // Captura excepciones de JWT inválido
        catch (JwtException | IllegalArgumentException e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /** Subject (correo). */
    public String extraerCorreo(String token) {
        return getClaims(token).getSubject();
    }

    /** Interno: claims. */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // No implementado: obtener token actual desde SecurityContextHolder
    public String obtenerTokenActual() {
        throw new UnsupportedOperationException("Método no implementado en JwtUtil. Usar SecurityContextHolder.");
    }
}

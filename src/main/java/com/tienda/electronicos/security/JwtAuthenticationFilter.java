package com.tienda.electronicos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filtro JWT: Authorization Bearer <token> -> Authentication con authorities de BD.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Dependencias
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    // Constructor
    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            UserDetailsServiceImpl userDetailsService,
            TokenBlacklistService tokenBlacklistService
    ) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    // Método principal del filtro
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();
        String normalizedPath = (path.endsWith("/") && path.length() > 1)
                ? path.substring(0, path.length() - 1)
                : path;

        boolean isProductosPublic = normalizedPath.equals("/api/productos") || normalizedPath.startsWith("/api/productos/");
        boolean isCategoriasPublic = normalizedPath.equals("/api/categorias") || normalizedPath.startsWith("/api/categorias/");
        boolean isPublicGetEndpoint = "GET".equals(method) && (isProductosPublic || isCategoriasPublic);

        boolean isAuthEndpoint = normalizedPath.startsWith("/api/auth/");

        log.debug("JWT filter - path='{}' method='{}' normalized='{}' isPublicGet='{}' isAuth='{}'",
                path, method, normalizedPath, isPublicGetEndpoint, isAuthEndpoint);

        if (isPublicGetEndpoint || isAuthEndpoint) {
            log.debug("Ruta pública detectada, continuando sin validar token: {} {}", method, path);
            filterChain.doFilter(request, response);
            return;
        }

        // ========================================
        // PROCESAR TOKEN JWT PARA RUTAS PROTEGIDAS
        // ========================================

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        try {
            if (token != null && tokenBlacklistService.isInvalid(token)) {
                log.debug("Token en blacklist, retornando 401");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (token != null
                    && jwtUtil.esTokenValido(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                String correo = jwtUtil.extraerCorreo(token);
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(correo);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                log.debug("Autenticación establecida para usuario: {}", correo);
            }
        }
        catch (Exception ex) {
            log.warn("Error validando token JWT: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
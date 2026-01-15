
package com.tienda.electronicos.security;

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
import java.io.IOException;

/**
 * Filtro JWT: Authorization Bearer <token> -> Authentication con authorities de BD.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Dependencias
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    // Constructor
    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            UserDetailsServiceImpl userDetailsService,
            TokenBlacklistService tokenBlacklistService
    )
    // Constructor manual en lugar de @RequiredArgsConstructor
    {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    // Método principal del filtro
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )

    // throws IOException, ServletException
            throws ServletException, IOException {

        // Extraer token del header Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;

        // Extraer token Bearer
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // Validar token y establecer Authentication
        try {
            // 🚫 Token cerrado por logout
            if (token != null && tokenBlacklistService.isInvalid(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // ✅ Token válido
            if (token != null
                    && jwtUtil.esTokenValido(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Extraer correo del token
                String correo = jwtUtil.extraerCorreo(token);
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(correo);

                // Crear Authentication
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                // Establecer detalles y contexto de seguridad
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Establecer en el contexto de seguridad
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }
        }
        // Capturar excepciones de validación del token
        catch (Exception ex) {
            // Se ignora → Spring manejará el 401/403
        }

        // Continuar con el filtro chain
        filterChain.doFilter(request, response);
    }
}


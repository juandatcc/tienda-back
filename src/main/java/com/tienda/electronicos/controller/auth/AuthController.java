package com.tienda.electronicos.controller.auth;

import com.tienda.electronicos.dto.auth.AuthResponse;
import com.tienda.electronicos.dto.auth.LoginRequest;
import com.tienda.electronicos.dto.auth.RegisterRequest;
import com.tienda.electronicos.security.TokenBlacklistService;
import com.tienda.electronicos.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    // ===== REGISTRO =====
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    // ===== LOGIN =====
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // ===== LOGOUT (INVALIDA TOKEN) =====
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader)
                && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            tokenBlacklistService.invalidate(token);
        }

        return ResponseEntity.noContent().build();
    }
}

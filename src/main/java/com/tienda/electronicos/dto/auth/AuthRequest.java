package com.tienda.electronicos.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para login
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// DTO para login
public class AuthRequest {
    private String correo;
    private String contrasena;
}

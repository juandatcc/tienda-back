package com.tienda.electronicos.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    // lo que responde el servidor al autenticar un usuario
    private String token;
    private String correo;
    private String rol;
}

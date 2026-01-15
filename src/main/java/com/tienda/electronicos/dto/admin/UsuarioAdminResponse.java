package com.tienda.electronicos.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// Response DTO para la información del usuario en el panel de administración
public class UsuarioAdminResponse {
    // ID del usuario
    private Long id;
    // Correo electrónico del usuario
    private String correo;
    private String nombre;
    private String telefono;
    private String direccion;
    private String rol;
}

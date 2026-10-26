package com.tienda.electronicos.dto.usuario;

import java.time.LocalDateTime;

// DTO de respuesta con los datos públicos de un usuario (nunca incluye la contraseña)
public record UsuarioResponse(
        // "id" e "idUsuario" llevan el mismo valor (el frontend lee cualquiera de los dos)
        String id,
        String idUsuario,
        String correo,
        String nombre,
        String telefono,
        String direccion,
        // Rol sin el prefijo ROLE_ (ADMIN / USER)
        String rol,
        LocalDateTime fechaRegistro
) {}

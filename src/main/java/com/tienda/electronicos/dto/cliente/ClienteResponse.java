package com.tienda.electronicos.dto.cliente;

// DTO de respuesta de Cliente (nunca expone la entidad Usuario ni su contraseña)
public record ClienteResponse(
        String id,
        String nombre,
        String direccion,
        String telefono,
        String idUsuario,
        String correo
) {}

package com.tienda.electronicos.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    // Método para obtener el correo electrónico del usuario autenticado
    public String obtenerCorreoDesdeContexto() {

        // Verificar si el usuario está autenticado
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        // Obtener el principal del contexto de seguridad
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Verificar si el principal es una instancia de User y retornar el nombre de usuario (correo)
        if (principal instanceof User user) {
            return user.getUsername();
        }

        // Si el principal no es válido, lanzar una excepción
        throw new RuntimeException("Principal no válido");
    }
}

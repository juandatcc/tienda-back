package com.tienda.electronicos.dto.admin;

// DTO para representar la respuesta de una categoría en el panel de administración
public record CategoriaAdminResponse(
        // Atributos de la categoría
        String idCategoria,
        String nombre,
        String descripcion
) {}

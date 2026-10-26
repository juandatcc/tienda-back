package com.tienda.electronicos.dto.product;

import java.math.BigDecimal;

// DTO para la respuesta de administración de productos
public record ProductoAdminResponse(
        String id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        String categoriaId,
        String categoriaNombre,
        String imagenUrl
)

    // Getters adicionales si es necesario
{
    public String getIdProducto() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Integer getStock() {
        return stock;
    }

    public ProductoAdminResponse getCategoria() {
        return null;
    }

    public String getIdCategoria() {
        return categoriaId;
    }

    public String getImagenUrl() { return imagenUrl; }
}

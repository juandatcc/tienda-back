package com.tienda.electronicos.dto.carrito;

import java.math.BigDecimal;

// Item del carrito tal como lo consume el frontend
public record CarritoItemResponse(
        String idItem,
        String idProducto,
        String nombreProducto,
        String descripcionProducto,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String imagenUrl
) {}

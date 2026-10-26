package com.tienda.electronicos.dto.carrito;

import java.math.BigDecimal;
import java.util.List;

// DTO DE RESPUESTA PARA EL CARRITO DE COMPRAS
public record CarritoResponse(
        String idCarrito,
        String idUsuario,
        BigDecimal total,
        List<CarritoItemResponse> items
) {}

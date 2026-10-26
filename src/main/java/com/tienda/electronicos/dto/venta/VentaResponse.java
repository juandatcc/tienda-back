package com.tienda.electronicos.dto.venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// DTO de respuesta de una venta (no expone entidades)
public record VentaResponse(
        String idVenta,
        LocalDateTime fecha,
        BigDecimal total,
        List<DetalleVentaResponse> detalles
) {

    // Línea de detalle de la venta
    public record DetalleVentaResponse(
            String idProducto,
            String nombreProducto,
            int cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal
    ) {}
}

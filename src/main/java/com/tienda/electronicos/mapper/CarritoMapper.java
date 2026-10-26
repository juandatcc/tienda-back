package com.tienda.electronicos.mapper;

import com.tienda.electronicos.dto.carrito.CarritoItemResponse;
import com.tienda.electronicos.dto.carrito.CarritoResponse;
import com.tienda.electronicos.entity.Carrito;
import com.tienda.electronicos.entity.Producto;

import java.math.BigDecimal;
import java.util.List;

public class CarritoMapper {

    private CarritoMapper() {
        // evita instanciación
    }

    // MAPEA DE Carrito A CarritoResponse
    public static CarritoResponse toResponse(Carrito carrito) {

        // MAPEA LOS ITEMS DEL CARRITO
        List<CarritoItemResponse> items = carrito.getItems()
                .stream()
                .map(item -> {
                    Producto p = item.getProducto();
                    return new CarritoItemResponse(
                            item.getIdItem(),
                            p.getIdProducto(),
                            p.getNombre(),
                            p.getDescripcion(),
                            item.getCantidad(),
                            item.getPrecioUnitario(),
                            item.getSubtotal(),
                            p.getImagenUrl()
                    );
                })
                .toList();

        // TOTAL = SUMA DE SUBTOTALES
        BigDecimal total = items.stream()
                .map(CarritoItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // RETORNA EL CarritoResponse COMPLETO
        return new CarritoResponse(
                carrito.getIdCarrito(),
                carrito.getUsuario() != null ? carrito.getUsuario().getIdUsuario() : null,
                total,
                items
        );
    }
}

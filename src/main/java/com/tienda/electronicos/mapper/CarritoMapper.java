package com.tienda.electronicos.mapper;

import com.tienda.electronicos.dto.carrito.CarritoItemResponse;
import com.tienda.electronicos.dto.carrito.CarritoResponse;
import com.tienda.electronicos.entity.Carrito;

import java.util.List;
import java.util.stream.Collectors;

public class CarritoMapper {

    private CarritoMapper() {
        // evita instanciación
    }

    // MAPEA DE Carrito A CarritoResponse
    public static CarritoResponse toResponse(Carrito carrito) {

        // MAPEA LOS ITEMS DEL CARRITO
        List<CarritoItemResponse> items = carrito.getItems()

                // AÑADE DESCRIPCION DEL PRODUCTO AL MAPPER
                .stream()

                // MAPEA CADA ITEM A CarritoItemResponse
                .map(item -> new CarritoItemResponse(
                        item.getProducto().getIdProducto(),
                        item.getProducto().getNombre(),
                        item.getProducto().getDescripcion(), // 👈 NUEVO
                        item.getCantidad(),
                        item.getProducto().getPrecio()
                ))
                .collect(Collectors.toList());

        // RETORNA EL CarritoResponse COMPLETO
        return new CarritoResponse(
                carrito.getIdCarrito(),
                items
        );
    }
}

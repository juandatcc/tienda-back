package com.tienda.electronicos.dto.carrito;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CarritoItemResponse {

    private Long productoId;
    private String nombreProducto;
    private String descripcionProducto;
    private int cantidad;
    private BigDecimal precio;


}

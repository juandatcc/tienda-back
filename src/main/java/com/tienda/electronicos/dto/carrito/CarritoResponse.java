package com.tienda.electronicos.dto.carrito;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
// DTO DE RESPUESTA PARA EL CARRITO DE COMPRAS
public class CarritoResponse {

    // ✅ ATRIBUTOS QUE TU MAPPER NECESITA
    private Long carritoId;
    private List<CarritoItemResponse> items;

    // ✅ CONSTRUCTOR QUE TU MAPPER NECESITA
    public CarritoResponse(Long carritoId, List<CarritoItemResponse> items)
    // Constructor
    {
        // Inicializa los atributos
        this.carritoId = carritoId;
        this.items = items;
    }

}

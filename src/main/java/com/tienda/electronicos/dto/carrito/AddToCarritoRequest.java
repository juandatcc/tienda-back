package com.tienda.electronicos.dto.carrito;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddToCarritoRequest {

    private Long productoId;
    private Integer cantidad;
}

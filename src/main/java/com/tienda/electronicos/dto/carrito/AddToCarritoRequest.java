package com.tienda.electronicos.dto.carrito;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Usado por agregar y actualizar. Acepta "idProducto" o el alias antiguo "productoId"
public class AddToCarritoRequest {

    @NotBlank(message = "El producto es obligatorio")
    @JsonAlias("productoId")
    private String idProducto;

    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;
}

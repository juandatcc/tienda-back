package com.tienda.electronicos.dto.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// DTO para crear o actualizar un cliente
public class ClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no debe superar 255 caracteres")
    private String nombre;

    @Size(max = 255, message = "La dirección no debe superar 255 caracteres")
    private String direccion;

    @Size(max = 50, message = "El teléfono no debe superar 50 caracteres")
    private String telefono;

    // Obligatorio solo al crear: usuario al que se asocia el cliente
    private String idUsuario;
}

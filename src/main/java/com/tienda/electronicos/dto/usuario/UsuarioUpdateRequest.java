package com.tienda.electronicos.dto.usuario;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// DTO para actualizar los datos personales de un usuario
public class UsuarioUpdateRequest {

    @Size(max = 255, message = "El nombre no debe superar 255 caracteres")
    private String nombre;

    // Opcional: si viene, debe tener exactamente 10 dígitos
    @Pattern(
            regexp = "^$|\\d{10}",
            message = "El teléfono debe contener exactamente 10 dígitos"
    )
    private String telefono;

    @Size(max = 255, message = "La dirección no debe superar 255 caracteres")
    private String direccion;
}

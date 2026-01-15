package com.tienda.electronicos.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email(message = "El correo no es válido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String contrasena;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no debe superar 100 caracteres")
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
            regexp = "\\d{10}",
            message = "El teléfono debe contener exactamente 10 dígitos"
    )
    private String telefono;


    @Size(max = 150, message = "La dirección no debe superar 150 caracteres")
    private String direccion;

    /** Si coincide con el código configurado, se asigna rol ADMIN; en otro caso USER. */
    private String codigoAdmin;
}

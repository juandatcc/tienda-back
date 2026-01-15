package com.tienda.electronicos.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

// DTO para la creación o actualización de una categoría
public class CategoriaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;


    @Size(max = 255, message = "La descripcion debe tener entre 50 y 255 caracteres")
    private String descripcion;
}

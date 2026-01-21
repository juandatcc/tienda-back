package com.tienda.electronicos.dto.product;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter @Setter

// DTO para la creación de un producto con imagen
public class ProductoCreateRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    // Descripción del producto
    @Size(max = 400, message = "La descripción no debe superar 400 caracteres")
    private String descripcion;

    // Precio del producto
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.00", inclusive = false, message = "El precio debe ser mayor que 0")
    private BigDecimal precio;

    // Stock del producto
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    // ID de la categoría a la que pertenece el producto
    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    // Imagen del producto (opcional)
    private MultipartFile imagen;
}


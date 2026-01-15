
package com.tienda.electronicos.dto.product;

import java.math.BigDecimal;


// DTO para la respuesta de Producto
public record ProductoResponse(
) {
    public void setIdProducto(Long ignoredIdProducto) {
    }

    public void setNombre(String ignoredNombre) {
    }

    public void setDescripcion(String ignoredDescripcion) {
    }

    public void setPrecio(BigDecimal ignoredPrecio) {
    }

    public void setStock(Integer ignoredStock) {
    }

    public void setCategoriaId(Long ignoredIdCategoria) {
    }

    public void setCategoriaNombre(String ignoredNombre) {
    }
}

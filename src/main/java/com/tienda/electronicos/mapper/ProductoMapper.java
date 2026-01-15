
package com.tienda.electronicos.mapper;

import com.tienda.electronicos.dto.product.ProductoAdminResponse;
import com.tienda.electronicos.dto.product.ProductoRequest;
import com.tienda.electronicos.dto.product.ProductoResponse;
import com.tienda.electronicos.entity.CategoriaProducto;
import com.tienda.electronicos.entity.Producto;
import org.mapstruct.*;

// Mapper para convertir entre Producto, ProductoRequest y ProductoResponse
@Mapper(componentModel = "spring")

// Configuración para ignorar campos nulos al actualizar
public interface ProductoMapper {
    // Mapea ProductoRequest a Producto entity
    @Mapping(target = "idProducto", ignore = true)
    // Mapea la categoría desde el parámetro separado
    @Mapping(target = "categoria", source = "categoria")
    // Convierte ProductoRequest a Producto entity
    Producto toEntity(ProductoRequest request, CategoriaProducto categoria);
    // Actualiza una entidad Producto existente con datos de ProductoRequest
    @Mapping(target = "idProducto", ignore = true)
    // Mapea la categoría desde el parámetro separado
    @Mapping(target = "categoria", source = "categoria")



    // Ignora campos nulos en la actualización
    void updateEntity(@MappingTarget Producto target, ProductoRequest request, CategoriaProducto categoria);
    // Mapea Producto entity a ProductoAdminResponse DTO
    @Mapping(target = "categoriaId", source = "categoria.idCategoria")
    // Mapea la categoría desde la entidad Producto
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    // Convierte Producto entity a ProductoAdminResponse DTO
    ProductoResponse toResponse(ProductoAdminResponse producto);
}


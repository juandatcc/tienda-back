package com.tienda.electronicos.mapper;

import com.tienda.electronicos.dto.product.ProductoAdminResponse;
import com.tienda.electronicos.dto.product.ProductoRequest;
import com.tienda.electronicos.dto.product.ProductoResponse;
import com.tienda.electronicos.entity.CategoriaProducto;
import com.tienda.electronicos.entity.Producto;
import org.springframework.stereotype.Component;

// Implementación del mapper para Producto
@Component
public class ProductoMapperImpl implements ProductoMapper {

    // Convertir ProductoRequest a entidad Producto
    @Override
    public Producto toEntity(ProductoRequest request, CategoriaProducto categoria) {
        if (request == null) return null;
        Producto p = new Producto();
        p.setNombre(request.getNombre());
        p.setDescripcion(request.getDescripcion());
        p.setPrecio(request.getPrecio());
        p.setStock(request.getStock());
        p.setCategoria(categoria);
        return p;
    }

    // Actualizar entidad Producto con datos de ProductoRequest
    @Override
    public void updateEntity(Producto target, ProductoRequest request, CategoriaProducto categoria) {
        if (target == null || request == null) return;
        target.setNombre(request.getNombre());
        target.setDescripcion(request.getDescripcion());
        target.setPrecio(request.getPrecio());
        target.setStock(request.getStock());
        target.setCategoria(categoria);
    }

    // Convertir ProductoAdminResponse a ProductoResponse
    @Override
    public ProductoResponse toResponse(ProductoAdminResponse producto) {
        if (producto == null) return null;
        ProductoResponse resp = new ProductoResponse();
        resp.setIdProducto(producto.getIdProducto());
        resp.setNombre(producto.getNombre());
        resp.setDescripcion(producto.getDescripcion());
        resp.setPrecio(producto.getPrecio());
        resp.setStock(producto.getStock());
        if (producto.getCategoria() != null) {
            resp.setCategoriaId(producto.getCategoria().getIdCategoria());
            resp.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        return resp;
    }
}

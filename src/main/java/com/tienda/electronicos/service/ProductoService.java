package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.product.ProductoAdminResponse;
import com.tienda.electronicos.dto.product.ProductoRequest;
import com.tienda.electronicos.entity.CategoriaProducto;
import com.tienda.electronicos.entity.Producto;
import com.tienda.electronicos.repository.CategoriaProductoRepository;
import com.tienda.electronicos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaRepository;

    // =========================
    // LISTAR (ADMIN / USER)
    // =========================
    @Transactional(readOnly = true)
    public List<ProductoAdminResponse> listarProductos() {
        return productoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // OBTENER POR ID
    // =========================
    @Transactional(readOnly = true)
    public ProductoAdminResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe")
                );
        return toResponse(producto);
    }

    // =========================
    // CREAR (ADMIN)
    // =========================
    public ProductoAdminResponse crearProducto(ProductoRequest request) {
        CategoriaProducto categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no existe")
                );

        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(categoria);

        productoRepository.save(producto);
        return toResponse(producto);
    }

    // =========================
    // ACTUALIZAR (ADMIN)
    // =========================
    public ProductoAdminResponse actualizarProducto(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe")
                );

        CategoriaProducto categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no existe")
                );

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(categoria);

        return toResponse(producto);
    }

    // =========================
    // ELIMINAR (ADMIN)
    // =========================
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe");
        }
        productoRepository.deleteById(id);
    }

    // =========================
    // MAPPER PRIVADO
    // =========================
    private ProductoAdminResponse toResponse(Producto p) {
        return new ProductoAdminResponse(
                p.getIdProducto(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getStock(),
                p.getCategoria().getIdCategoria(),
                p.getCategoria().getNombre()
        );
    }
}

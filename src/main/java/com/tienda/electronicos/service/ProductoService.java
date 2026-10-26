package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.product.ProductoAdminResponse;
import com.tienda.electronicos.dto.product.ProductoCreateRequest;
import com.tienda.electronicos.dto.product.ProductoRequest;
import com.tienda.electronicos.entity.CategoriaProducto;
import com.tienda.electronicos.entity.Producto;
import com.tienda.electronicos.repository.CategoriaProductoRepository;
import com.tienda.electronicos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaRepository;
    private final ImagenService imagenService;

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
    public ProductoAdminResponse obtenerPorId(String id) {
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
        producto.setImagenUrl(request.getImagenUrl());

        productoRepository.save(producto);
        return toResponse(producto);
    }

    // =========================
    // CREAR CON IMAGEN (ADMIN)
    // =========================
    public ProductoAdminResponse crearProductoConImagen(ProductoCreateRequest request) {
        // Validar categoría
        CategoriaProducto categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría no encontrada"));

        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .categoria(categoria)
                .build();
        // Se guarda primero para tener el id con el que se organiza la imagen en el bucket
        producto = productoRepository.save(producto);

        // Subir imagen a Supabase si viene; si falla, la transacción revierte el producto
        if (request.getImagen() != null && !request.getImagen().isEmpty()) {
            producto.setImagenUrl(imagenService.subirImagenProducto(request.getImagen()));
            producto = productoRepository.save(producto);
        }
        return toResponse(producto);
    }

    // =========================
    // ACTUALIZAR (ADMIN)
    // =========================
    public ProductoAdminResponse actualizarProducto(String id, ProductoRequest request) {
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
        producto.setImagenUrl(request.getImagenUrl());

        productoRepository.save(producto);
        return toResponse(producto);
    }

    // =========================
    // SUBIR IMAGEN (ADMIN) - se guarda en Supabase Storage y la URL pública en IMAGEN_URL
    // =========================
    public ProductoAdminResponse uploadImagen(String id, MultipartFile file) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe"));

        String imagenUrl = imagenService.subirImagenProducto(file);
        producto.setImagenUrl(imagenUrl);
        productoRepository.save(producto);
        log.info("Producto {} actualizado con imagenUrl={}", producto.getIdProducto(), imagenUrl);
        return toResponse(producto);
    }

    // =========================
    // ELIMINAR (ADMIN)
    // =========================
    public void eliminarProducto(String id) {
        if (!productoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no existe");
        }
        productoRepository.deleteById(id);
    }

    // =========================
    // MAPPER PRIVADO
    // =========================
    private ProductoAdminResponse toResponse(Producto p) {
        String categoriaId = p.getCategoria() != null ? p.getCategoria().getIdCategoria() : null;
        String categoriaNombre = p.getCategoria() != null ? p.getCategoria().getNombre() : "Sin categoría";
        return new ProductoAdminResponse(
                p.getIdProducto(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getStock(),
                categoriaId,
                categoriaNombre,
                p.getImagenUrl()
        );
    }
}
